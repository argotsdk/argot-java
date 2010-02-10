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
package com.argot.meta;

import java.util.HashMap;
import java.util.Map;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;

public class MetaCluster 
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME = "meta.cluster";
	
	private Map _cluster;
	private Object _entry;
	
	public MetaCluster()
	{
		_cluster = new HashMap();

	}
	
	public void bind(TypeLibrary library, int memberTypeId, TypeLocation location, TypeElement definition) 
	throws TypeException 
	{
		//super.bind(library, memberTypeId, location, definition);
	}

	public String getTypeName() 
	{
		return TYPENAME;
	}


	public void put( String name, Object object )
	{
		_cluster.put(name, object);
	}
	
	public Object get( String name )
	{
		return _cluster.get( name );
	}

	public Object entry()
	{
		return _entry;
	}

	public void setEntry(Object entry)
	{
		_entry = entry;
	}


}