/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot.network;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.argot.TypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeOutputStream;
import com.argot.common.Int32;
import com.argot.common.U8Boolean;
import com.argot.common.UInt8;
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryLocation;
import com.argot.remote.MetaObject;

public class TypeServer
implements TypeLink
{
	private TypeMap _typeMap;
	private TypeMap _refMap;
	private TypeLibrary _library;
	private MetaObject _object;
	private TypeLink _service;
	

	public TypeServer( TypeLibrary library, TypeMap refMap, TypeLink service )
	throws TypeException
	{
		_typeMap = new TypeMap( library, new ProtocolTypeMapper() );
		_refMap = refMap;
		_typeMap.setReference(TypeMap.REFERENCE_MAP,_refMap);
		_library = library;
		_service = service;
	}

	public TypeServer( TypeLibrary library, TypeMap refMap )
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
					
			if ( action == ProtocolTypeMapper.MAP )
			{
				processMap( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.MAPDEF )
			{
				processMapDefault( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.MAPRES )
			{
				processMapReserve( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.MAPREV )
			{
				processMapReverse( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.BASE )
			{
				processGetBaseObject( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.MSG )
			{
				processUserMessage( in, out );
				return;
			}
			else if ( action == ProtocolTypeMapper.CHECK_CORE )
			{
				processCheckCore( in, out );
				return;
			}
			
			// return an error array.
			TypeOutputStream sout = new TypeOutputStream( connection.getOutputStream(), _typeMap );
			sout.writeObject( "uint8", new Short( ProtocolTypeMapper.ERROR ) );
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

		TypeLocation location;
		try
		{
			location = _refMap.getLocation( id.intValue() );
		}
		catch (TypeException e)
		{
			sout.writeObject( "uint8", new Short( ProtocolTypeMapper.ERROR ) );
			sout.getStream().flush();
			return;
		}
		TypeElement struct = _refMap.getStructure( id.intValue() );

		byte[] definition = TypeHelper.toByteArray( _refMap, struct );
		
		sout.writeObject( "uint8", new Short( ProtocolTypeMapper.MAPREV ) );
		sout.writeObject( DictionaryLocation.TYPENAME, location );
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
		TypeLocation location = (TypeLocation) in.readObject(DictionaryLocation.TYPENAME);
		location = TypeTriple.fixLocation(location, _refMap);
		
		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );

		sout.writeObject( "uint8", new Short( ProtocolTypeMapper.MAPRES ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getTypeId( location );
		if ( systemId == TypeLibrary.NOTYPE )
		{
			sout.writeObject( Int32.TYPENAME, new Integer(-1));
		}
		else
		{
			int id;
			
			// This will find the id and it is is not yet 
			// mapped it will
			id = _refMap.getStreamId( systemId );
			
			sout.writeObject( Int32.TYPENAME, new Integer(id));
			
		}		
		
		out.flush();
	}
	
	private TypeLocation getLocation(byte[] buffer) 
	throws TypeException, IOException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream(buffer);
		TypeInputStream tis = new TypeInputStream(bais, _typeMap);
		return (TypeLocation) tis.readObject( DictionaryLocation.TYPENAME );
	}
	
	private void processMapDefault( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{
		byte[] buffer = (byte[]) in.readObject("u16binary");
		TypeLocation location = getLocation(buffer);
		location = TypeTriple.fixLocation(location, _refMap);

		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );

		int definitionId;
		int mapId;
		try
		{
			definitionId = _refMap.getLibrary().getTypeId(location);
		}
		catch (TypeException e)
		{
			sout.writeObject( "uint8", new Short( ProtocolTypeMapper.ERROR ) );
			sout.getStream().flush();
			return;			
		}
		
		try
		{
			mapId = _refMap.getStreamId(definitionId);
		}
		catch (TypeException e)
		{
			sout.writeObject( "uint8", new Short( ProtocolTypeMapper.ERROR ) );
			sout.getStream().flush();
			return;
		}
		TypeElement struct = _refMap.getStructure( mapId );

		TypeLocation defLocation = _refMap.getLocation(mapId);
		byte[] definition = TypeHelper.toByteArray( _refMap, struct );
		
		sout.writeObject( "uint8", new Short( ProtocolTypeMapper.MAPDEF ) );
		sout.writeObject( Int32.TYPENAME, new Integer(mapId));
		sout.writeObject( DictionaryLocation.TYPENAME, defLocation );
		sout.writeObject( "u16binary", definition );

		out.flush();
		
	}

	private void processMap( TypeInputStream in, OutputStream out )
	throws TypeException, IOException
	{
		byte[] buffer = (byte[]) in.readObject("u16binary");
		TypeLocation location = getLocation(buffer);
		location = TypeTriple.fixLocation(location, _refMap);
		
		byte[] def = (byte[]) in.readObject( "u16binary" );
		
		// Use the type name to compare the definition that was sent.
		// If they match we can allocate a new id and return.  If
		// they don't match then we can't agree on the type definition
		// and will return an invalid id.
		
		TypeOutputStream sout = new TypeOutputStream( out, _typeMap );
		sout.writeObject( "uint8", new Short( ProtocolTypeMapper.MAP ) );			
		
		// First see if we have a type of the same name.
		int systemId = _library.getTypeId( location );
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
				
				id = _refMap.getStreamId( systemId );

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
		sout.writeObject( "uint8", new Short( ProtocolTypeMapper.BASE ) );			
		
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
		sout.writeObject( UInt8.TYPENAME, new Short( ProtocolTypeMapper.CHECK_CORE ) );			
		sout.writeObject( U8Boolean.TYPENAME, new Boolean( metaEqual ) );

		out.flush();
	}
}
