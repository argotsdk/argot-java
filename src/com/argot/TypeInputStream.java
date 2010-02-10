/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
package com.argot;

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an InputStream helper which reads objects directly off the
 * stream.
 */
public class TypeInputStream
{
	private TypeMap _map;
	private InputStream _in;

	public TypeInputStream( InputStream in, TypeMap map )
	{
		_in = in;
		_map = map;	
	}
	
	public InputStream getStream()
	{
		return _in;
	}	
	
	public TypeMap getTypeMap()
	{
		return _map;
	}
	
	public void setTypeMap( TypeMap map )
	{
		_map = map;
	}

	public Object readObject( int streamId )
	throws TypeException, IOException
	{		
		TypeReader reader = _map.getReader( streamId );
		
		try
		{
			return reader.read( this );
		}
		catch (TypeStreamException readEx)
		{
			readEx.addTypeName( _map.getName( streamId ).toString());
			throw readEx;
		}
		catch (IOException ioEx )
		{
			throw new TypeStreamException( _map.getName(streamId).toString(), ioEx );
		}
	}

	public Object readObject( String name )
	throws TypeException, IOException
	{
		int id = _map.getStreamId( name );
		if ( id == TypeLibrary.NOTYPE )
			throw new TypeException( "type not registered");
		
		TypeReader reader = _map.getReader( id );
		
		try
		{
			return reader.read( this );
		}
		catch (TypeStreamException readEx)
		{
			readEx.addTypeName( _map.getName( id ).toString());
			throw readEx;
		}
		catch (IOException ioEx)
		{
			throw new TypeStreamException( _map.getName(id).getFullName(), ioEx );			
		}
	}

	/**
	 * Helper method that will throw an exception if the input stream closes.
	 * @return
	 * @throws TypeException
	 * @throws IOException
	 */
	public int read()
	throws TypeException,IOException
	{
		int b = _in.read();
		if (b == -1)
			throw new TypeException("TypeInputStream: input stream closed");
		return b;
	}
	
	/**
	 * Helper method that will throw an exception if all the bytes are not read.
	 * Required because InputStream.read can return without filling the full buffer.
	 * 
	 * @param buffer
	 * @param offset
	 * @param count
	 * @return
	 * @throws TypeException
	 * @throws IOException
	 */
	public int read(byte[] buffer,int offset,int count)
	throws TypeException,IOException
	{
		int read = 0;
		while(count>0)
		{
			read += _in.read(buffer,offset,count);
			count-=read;
			offset+=read;
		}
		return read;
	}
}
