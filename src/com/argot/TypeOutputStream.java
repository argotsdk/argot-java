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
import java.io.OutputStream;

/**
 * An output stream class which allows Objects to be written to the 
 * stream using the type system.
 */
public class TypeOutputStream
{
	private TypeMap _map;
	private OutputStream _out;
	
	public TypeOutputStream( OutputStream out, TypeMap map )
	{
		_out = out;
		_map = map;	
	}
	
	public OutputStream getStream()
	{
		return _out;
	}

	public TypeMap getTypeMap()
	{
		return _map;
	}

	public void writeObject( int id, Object o )
	throws TypeException, IOException
	{
		TypeWriter writer = _map.getWriter( id );
		writer.write( this, o );
	}
	
	public void writeObject( String name, Object o )
	throws TypeException, IOException
	{
		int id = this.getTypeMap().getStreamId( name );
		if ( id == TypeLibrary.NOTYPE )
			throw new TypeException( "not found: " + name);
		
		TypeWriter writer = _map.getWriter( id );
		writer.write( this, o );
	}

}
