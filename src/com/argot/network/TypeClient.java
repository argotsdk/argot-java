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
import java.io.InputStream;
import java.io.OutputStream;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.TypeWriter;
import com.argot.common.Int32;
import com.argot.common.UInt8;
import com.argot.common.U8Boolean;
import com.argot.remote.MetaObject;
import com.argot.util.ChunkByteBuffer;

public class TypeClient
implements TypeTransport
{
	private TypeTransport _link;
	private ProtocolTypeMap _typeMap;
	private TypeWriter _uint8;

	public TypeClient( TypeLibrary library, TypeTransport link )
	throws TypeException
	{
		_link = link;
		_typeMap = new ProtocolTypeMap( library );
		_uint8 = library.getWriter(library.getId("uint8")).getWriter(_typeMap);
	}

	/*
	 * The idea behind TypeClient implementing TypeTransport is to allow
	 * higher level user protocols running on the stack.  This protocol
	 * is a request response system.  To provide a similar programmer
	 * API, the openLink returns an EndPoint which buffers the request.
	 * When the user starts to read from the input, it triggers a send of
	 * the full request to the server.  The response is also buffered and
	 * provided to the client.
	 * 
	 * It might be possible to offer a true pipe later on which gives the
	 * client correct asynchronous communications.  That can happen later though.
	 */
	public TypeEndPoint openLink() throws IOException
	{
		TypeEndPoint ep = _link.openLink();
		
		try {
			TypeOutputStream tmos = new TypeOutputStream( ep.getOutputStream(), _typeMap );
			_uint8.write(tmos, new Short(ProtocolTypeMap.MSG));
		} catch (TypeException e) {
			// Java 1.4 only has simple IOException constructors.
			throw new IOException(e.toString());
		}
		
		return ep;
	}

	public void closeLink( TypeEndPoint endPoint )
	{
		_link.closeLink( endPoint );		
	}
	
	public class TypeClientInputStream
	extends InputStream
	{
		private TypeEndPoint _transport;
		private ChunkByteBuffer _buffer;
		private InputStream _stream;
		private boolean _reading;
		private boolean _error;
		
		public TypeClientInputStream( TypeEndPoint transport, ChunkByteBuffer buffer )
		{
			_transport = transport;
			_buffer = buffer;
			_reading = false;
			_error = false;
		}

		public int read() 
		throws IOException
		{
			if ( !_reading )
			{
				_reading = true;
				performRequest();
			}
			
			if ( _error ) return -1;
			
			return _stream.read();
		}
		
		public int read( byte[] buffer, int start, int end ) 
		throws IOException
		{
			if ( !_reading )
			{
				_reading = true;
				performRequest();
			}
			
			if ( _error ) return -1;

			return _stream.read(buffer, start, end);
		}		
		
		private void performRequest() 
		throws IOException
		{
						
			try
			{
				_buffer.close();
				OutputStream out = _transport.getOutputStream();
				TypeOutputStream tmos = new TypeOutputStream( out, _typeMap );
				tmos.writeObject( "u8", new Short(ProtocolTypeMap.MSG) );
				tmos.writeObject( "u32binary", _buffer );		
				
				InputStream in = _transport.getInputStream();
				TypeInputStream tmis = new TypeInputStream( in, _typeMap );
				Short type = (Short) tmis.readObject( UInt8.TYPENAME );
				if ( type.intValue() != ProtocolTypeMap.MSG )throw new IOException("Bad Protocol Error"); 
				_buffer = (ChunkByteBuffer) tmis.readObject( "u32binary" );
				_stream = _buffer.getInputStream();
			}
			catch (TypeException e)
			{
				_error = true;
				// Java 1.4 only has simple IOException constructors.
				throw new IOException( e.toString() );
			}
			catch (IOException e)
			{
				_error = true;
				throw e;
			}				
			
		}

		
	}

	public boolean checkMetaDictionary( byte[] metaDictionary )
	throws IOException, TypeException
	{
		TypeEndPoint endPoint = _link.openLink();
		
		try
		{
			// Write the name and definition to the request body.	
			OutputStream out = endPoint.getOutputStream();
			TypeOutputStream tmos = new TypeOutputStream( out, _typeMap );
			tmos.writeObject( "uint8", new Short(ProtocolTypeMap.CHECK_CORE ) );
			tmos.writeObject( "u16binary", metaDictionary );		
			tmos.getStream().flush();
			
			InputStream in = endPoint.getInputStream();
			TypeInputStream tmis = new TypeInputStream( in, _typeMap );
			Short type = (Short) tmis.readObject( UInt8.TYPENAME );
			if ( type.intValue() != ProtocolTypeMap.CHECK_CORE )throw new TypeException("Bad Protocol Error"); 
			Boolean value = (Boolean) tmis.readObject( U8Boolean.TYPENAME );

			return value.booleanValue();
		}
		finally
		{
			_link.closeLink( endPoint );
		}
		
	}
	
	public int resolveType( String name, byte[] definition )
	throws IOException, TypeException
	{
		TypeEndPoint endPoint = _link.openLink();
		
		try
		{
			// Write the name and definition to the request body.	
			OutputStream out = endPoint.getOutputStream();
			TypeOutputStream tmos = new TypeOutputStream( out, _typeMap );
			tmos.writeObject( "uint8", new Short(ProtocolTypeMap.MAP) );
			tmos.writeObject( "u8ascii", name );
			tmos.writeObject( "u16binary", definition );		
			tmos.getStream().flush();
			
			InputStream in = endPoint.getInputStream();
			TypeInputStream tmis = new TypeInputStream( in, _typeMap );
			Short type = (Short) tmis.readObject( UInt8.TYPENAME );
			if ( type.intValue() != ProtocolTypeMap.MAP )throw new TypeException("Bad Protocol Error"); 
			Integer value = (Integer) tmis.readObject( Int32.TYPENAME );

			return value.intValue();
		}
		finally
		{
			_link.closeLink( endPoint );
		}
	}

	public int reserveType( String name )
	throws IOException, TypeException
	{		
		TypeEndPoint endPoint = _link.openLink();
		
		try
		{
			// Write the name and definition to the request body.	
			TypeOutputStream tmos = new TypeOutputStream( endPoint.getOutputStream(), _typeMap );
			tmos.writeObject( "uint8", new Short(ProtocolTypeMap.MAPRES) );		
			tmos.writeObject( "u8ascii", name );
			tmos.getStream().flush();
			
			TypeInputStream tmis = new TypeInputStream( endPoint.getInputStream(), _typeMap );
			Short type = (Short) tmis.readObject( UInt8.TYPENAME );
			if ( type.intValue() != ProtocolTypeMap.MAPRES )throw new TypeException("Bad Protocol Error");		
			Integer value = (Integer) tmis.readObject( Int32.TYPENAME );
	
			return value.intValue();
		}
		finally
		{
			_link.closeLink( endPoint );
		}
	}

	public TypeTriple resolveReverse( int id )
	throws IOException, TypeException
	{		
		TypeEndPoint endPoint = _link.openLink();
		
		try
		{
			// Write the name and definition to the request body.
			TypeOutputStream tmos = new TypeOutputStream( endPoint.getOutputStream(), _typeMap );
			tmos.writeObject( "uint8", new Short(ProtocolTypeMap.MAPREV) );		
			tmos.writeObject( Int32.TYPENAME, new Integer( id ) );
			tmos.getStream().flush();
			
			TypeInputStream tmis = new TypeInputStream( endPoint.getInputStream(), _typeMap );
			Short type = (Short) tmis.readObject( UInt8.TYPENAME );
			if ( type.intValue() != ProtocolTypeMap.MAPREV )throw new TypeException("Unable to resolve reverse id: " + id);		
			String name = (String) tmis.readObject( "u8ascii" );
			byte[] definition = (byte[]) tmis.readObject( "u16binary" );
			
			return new TypeTriple( id, name, definition );
		}
		finally
		{
			_link.closeLink( endPoint );
		}
	}
	
	public MetaObject getBaseObject( TypeMap map )
	throws IOException, TypeException
	{		
		TypeEndPoint endPoint = _link.openLink();
		
		try
		{
			// Write the name and definition to the request body.
			TypeOutputStream tmos = new TypeOutputStream( endPoint.getOutputStream(), _typeMap );
			tmos.writeObject( "uint8", new Short(ProtocolTypeMap.BASE) );		
			tmos.getStream().flush();
			
			TypeInputStream tmis = new TypeInputStream( endPoint.getInputStream(), map );
			Short type = (Short) tmis.readObject( UInt8.TYPENAME );
			if ( type.intValue() != ProtocolTypeMap.BASE )throw new TypeException("Bad Protocol Error");		
			Boolean value = (Boolean) tmis.readObject( U8Boolean.TYPENAME );
			if ( !value.booleanValue() )
			{
				return null;
			}
			MetaObject object = (MetaObject) tmis.readObject( MetaObject.TYPENAME );
			
			return object;
		}
		finally
		{
			_link.closeLink( endPoint );
		}
	}
}
