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

	public Object readObject( int id )
	throws TypeException, IOException
	{		
		TypeReader reader = _map.getReader( id );
		TypeElement element = _map.getStructure( id );
		return reader.read( this, element );
	}
	
	public Object readObject( String name )
	throws TypeException, IOException
	{
		int id = _map.getId( name );
		if ( id == TypeMap.NOTYPE )
			throw new TypeException( "type not registered");
			
		TypeReader reader = _map.getReader( id );
		TypeElement element = _map.getStructure( id );
		return reader.read( this, element );
	}
}
