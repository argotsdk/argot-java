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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.DynamicTypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;

import com.argot.common.BigEndianSignedInteger;
import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.U8Boolean;

import com.argot.remote.MetaObject;

public class TypeServer
implements TypeLink
{
	private ProtocolTypeMap _typeMap;
	private DynamicTypeMap _refMap;
	private TypeLibrary _library;
	private MetaObject _object;
	

	public TypeServer( TypeLibrary library, DynamicTypeMap refMap )
	throws TypeException
	{
		_typeMap = new ProtocolTypeMap( library );
		_refMap = refMap;
		_library = library;
	}

	public void setBaseObject( MetaObject object )
	{
		_object = object;
	}

	public byte[] processMessage( byte[] request )
	throws IOException
	{
		
		try
		{
			ByteArrayInputStream bais = new ByteArrayInputStream( request );
			TypeInputStream in = new TypeInputStream( bais, _typeMap );
			Object o = in.readObject( BigEndianUnsignedByte.TYPENAME );
			int action = ((Short) o).intValue();
					
			if ( action == ProtocolTypeMap.MAP )
			{
				return processMap( in );
			}
			else if ( action == ProtocolTypeMap.MAPRES )
			{
				return processMapReserve( in );
			}
			else if ( action == ProtocolTypeMap.MAPREV )
			{
				return processMapReverse( in );
			}
			else if ( action == ProtocolTypeMap.BASE )
			{
				return processGetBaseObject( in );
			}
			
			// return an error array.
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			TypeOutputStream sout = new TypeOutputStream( baos, _typeMap );
			sout.writeObject( "u8", new Short( ProtocolTypeMap.ERROR ) );
			baos.flush();
			baos.close();			
			return baos.toByteArray();
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
	private byte[] processMapReverse( TypeInputStream in )
	throws TypeException, IOException
	{
		Integer id = (Integer) in.readObject( BigEndianSignedInteger.TYPENAME );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream sout = new TypeOutputStream( baos, _typeMap );

		String name = _refMap.getName( id.intValue() );
		TypeElement struct = _refMap.getStructure( id.intValue() );

		byte[] definition = TypeHelper.toByteArray( _refMap, struct );
		
		sout.writeObject( "u8", new Short( ProtocolTypeMap.MAPREV ) );
		sout.writeObject( "u8ascii", name );
		sout.writeObject( "u16binary", definition );

		baos.flush();
		baos.close();
		
		return baos.toByteArray();
	}
	
	/*
	 * MapReserve is for situations where the client is attempting
	 * to resolve a type which includes its own definition.  To
	 * resolve this situation it reserves an ID for the type by
	 * sending the name and receiving an ID.  The server may not
	 * have the type and return -1.
	 */
	private byte[] processMapReserve( TypeInputStream in )
	throws TypeException, IOException
	{	
		String name = (String) in.readObject( "u8ascii" );

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream sout = new TypeOutputStream( baos, _typeMap );

		sout.writeObject( "u8", new Short( ProtocolTypeMap.MAPRES ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getId( name );
		if ( systemId == TypeLibrary.NOTYPE )
		{
			sout.writeObject( BigEndianSignedInteger.TYPENAME, new Integer(-1));
		}
		else
		{
			int id;
			
			// This will find the id and it is is not yet 
			// mapped it will
			id = _refMap.getId( name );
			
			sout.writeObject( BigEndianSignedInteger.TYPENAME, new Integer(id));
			
		}		
		
		baos.flush();
		baos.close();
		
		return baos.toByteArray();
	}

	private byte[] processGetBaseObject( TypeInputStream request )
	throws TypeException, IOException
	{		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream sout = new TypeOutputStream( baos, _refMap );

		sout.writeObject( "u8", new Short( ProtocolTypeMap.BASE ) );			
		
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
		
		baos.flush();
		baos.close();

		return baos.toByteArray();
	}
	
	private byte[] processMap( TypeInputStream in )
	throws TypeException, IOException
	{
		String name = (String) in.readObject( "u8ascii" );
		byte[] def = (byte[]) in.readObject( "u16binary" );
		
		// Use the type name to compare the definition that was sent.
		// If they match we can allocate a new id and return.  If
		// they don't match then we can't agree on the type definition
		// and will return an invalid id.
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream sout = new TypeOutputStream( baos, _typeMap );
		sout.writeObject( "u8", new Short( ProtocolTypeMap.MAP ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getId( name );
		if ( systemId == TypeLibrary.NOTYPE )
		{
			sout.writeObject( BigEndianSignedInteger.TYPENAME, new Integer(-1));
		}
		else
		{
			// Check the structures are the same.
		    TypeElement struct = _library.getStructure( systemId );

		    if ( TypeHelper.structureMatches( _refMap, struct, def ))
			{
				int id;
				
				id = _refMap.getId( name );

				sout.writeObject( BigEndianSignedInteger.TYPENAME, new Integer(id));
				
			}
			else
			{
				// Return an invalid id.
				sout.writeObject( BigEndianSignedInteger.TYPENAME, new Integer(-1));
			}
		}

		baos.flush();
		baos.close();
		
		return baos.toByteArray();
	}

}
