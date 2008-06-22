/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
 */
package com.argot.network;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.argot.DynamicTypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeMapCore;
import com.argot.TypeOutputStream;
import com.argot.common.Int32;
import com.argot.common.UInt8;
import com.argot.common.U8Boolean;
import com.argot.dictionary.Dictionary;
import com.argot.remote.MetaObject;

public class TypeServer
implements TypeLink
{
	private ProtocolTypeMap _typeMap;
	private DynamicTypeMap _refMap;
	private TypeLibrary _library;
	private MetaObject _object;
	private TypeLink _service;
	

	public TypeServer( TypeLibrary library, DynamicTypeMap refMap, TypeLink service )
	throws TypeException
	{
		_typeMap = new ProtocolTypeMap( library );
		_refMap = refMap;
		_library = library;
		_service = service;
		/*
		TypeMapCore.mapMeta( refMap, library );
		refMap.map( 22, library.getId("dictionary.map"));
		refMap.map( 23, library.getId("dictionary.words"));
		refMap.map( 24, library.getId("dictionary.definition"));
		refMap.map( 25, library.getId("dictionary.entry"));	
		refMap.map( 26, library.getId("meta.envelop"));
		refMap.map( 27, library.getId("meta.definition#envelop"));		
		*/
	}

	public TypeServer( TypeLibrary library, DynamicTypeMap refMap )
	throws TypeException
	{
		this( library, refMap, null );
	}
	
	public void setBaseObject( MetaObject object )
	{
		_object = object;
	}

	public void processMessage( TypeEndPoint connection )
	throws IOException
	{
		
		try
		{
			OutputStream out = connection.getOutputStream();
			TypeInputStream in = new TypeInputStream( connection.getInputStream(), _typeMap );
			Object o = in.readObject( UInt8.TYPENAME );
			int action = ((Short) o).intValue();
					
			if ( action == ProtocolTypeMap.MAP )
			{
				processMap( in, out );
				return;
			}
			else if ( action == ProtocolTypeMap.MAPRES )
			{
				processMapReserve( in, out );
				return;
			}
			else if ( action == ProtocolTypeMap.MAPREV )
			{
				processMapReverse( in, out );
				return;
			}
			else if ( action == ProtocolTypeMap.BASE )
			{
				processGetBaseObject( in, out );
				return;
			}
			else if ( action == ProtocolTypeMap.MSG )
			{
				processUserMessage( in, out );
				return;
			}
			else if ( action == ProtocolTypeMap.CHECK_CORE )
			{
				processCheckCore( in, out );
				return;
			}
			
			// return an error array.
			TypeOutputStream sout = new TypeOutputStream( connection.getOutputStream(), _typeMap );
			sout.writeObject( "uint8", new Short( ProtocolTypeMap.ERROR ) );
			sout.getStream().flush();
		}	
		catch (TypeException e)
		{
			e.printStackTrace();
			throw new IOException("exception reading data");
		}
	}
	
	/*
	 * MapReverse is the situation where an identifier has
	 * already been mapped on the server but the client doesn't
	 * know about it.  The client sends the id and recieves
	 * the type name and type definition.
	 */
	private void processMapReverse( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{
		Integer id = (Integer) in.readObject( Int32.TYPENAME );

		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );

		String name;
		try
		{
			name = _refMap.getName( id.intValue() );
		}
		catch (TypeException e)
		{
			sout.writeObject( "uint8", new Short( ProtocolTypeMap.ERROR ) );
			sout.getStream().flush();
			return;
		}
		TypeElement struct = _refMap.getStructure( id.intValue() );

		byte[] definition = TypeHelper.toByteArray( _refMap, struct );
		
		sout.writeObject( "uint8", new Short( ProtocolTypeMap.MAPREV ) );
		sout.writeObject( "u8ascii", name );
		sout.writeObject( "u16binary", definition );

		out.flush();
	}
	
	/*
	 * MapReserve is for situations where the client is attempting
	 * to resolve a type which includes its own definition.  To
	 * resolve this situation it reserves an ID for the type by
	 * sending the name and receiving an ID.  The server may not
	 * have the type and return -1.
	 */
	private void processMapReserve( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{	
		String name = (String) in.readObject( "u8ascii" );

		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );

		sout.writeObject( "uint8", new Short( ProtocolTypeMap.MAPRES ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getId( name );
		if ( systemId == TypeLibrary.NOTYPE )
		{
			sout.writeObject( Int32.TYPENAME, new Integer(-1));
		}
		else
		{
			int id;
			
			// This will find the id and it is is not yet 
			// mapped it will
			id = _refMap.getId( name );
			
			sout.writeObject( Int32.TYPENAME, new Integer(id));
			
		}		
		
		out.flush();
	}

	private void processMap( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{
		String name = (String) in.readObject( "u8ascii" );
		byte[] def = (byte[]) in.readObject( "u16binary" );
		
		// Use the type name to compare the definition that was sent.
		// If they match we can allocate a new id and return.  If
		// they don't match then we can't agree on the type definition
		// and will return an invalid id.
		
		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );
		sout.writeObject( "uint8", new Short( ProtocolTypeMap.MAP ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getId( name );
		if ( systemId == TypeLibrary.NOTYPE )
		{
			sout.writeObject( Int32.TYPENAME, new Integer(-1));
		}
		else
		{
			// Check the structures are the same.
		    TypeElement struct = _library.getStructure( systemId );

		    if ( TypeHelper.structureMatches( _refMap, struct, def ))
			{
				int id;
				
				id = _refMap.getId( name );

				sout.writeObject( Int32.TYPENAME, new Integer(id));
				
			}
			else
			{
				// Return an invalid id.
				sout.writeObject( Int32.TYPENAME, new Integer(-1));
			}
		}

		out.flush();
	}

	private void processGetBaseObject( TypeInputStream request, OutputStream out )
	throws TypeException, IOException
	{		
		TypeOutputStream sout = new TypeOutputStream( out, _refMap );
		sout.writeObject( "uint8", new Short( ProtocolTypeMap.BASE ) );			
		
		// Check base name.
		if ( _object == null )
		{
			sout.writeObject( U8Boolean.TYPENAME, new Boolean(false));
		}
		else
		{					
			sout.writeObject( U8Boolean.TYPENAME, new Boolean(true));
			sout.writeObject( MetaObject.TYPENAME, _object );
		}
		
		out.flush();
	}

	/*
	 * A user message gets passed to the available service running on top of
	 * the TypeServer.  This grabs the data from the client and stores it in a
	 * ChunkByteBuffer and then passes the buffer input stream to the service.
	 * The response is buffered until the service returns and the response
	 * sent.
	 */
	private void processUserMessage( TypeInputStream request, OutputStream out )
	throws TypeException, IOException
	{
		TypeEndPoint ep = new TypeEndPointBasic( request.getStream(), out );
		_service.processMessage( ep );
		out.flush();
	}
	
	private void processCheckCore( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{
		byte[] clientMetaDictionary = (byte[]) in.readObject( "u16binary" );
		
		byte[] serverMetaDictionary = Dictionary.writeCore( _refMap );
		boolean metaEqual = Arrays.equals( clientMetaDictionary, serverMetaDictionary );
		
		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );
		sout.writeObject( UInt8.TYPENAME, new Short( ProtocolTypeMap.CHECK_CORE ) );			
		sout.writeObject( U8Boolean.TYPENAME, new Boolean( metaEqual ) );

		out.flush();
	}
}
