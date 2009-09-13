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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.U8Ascii;

public class MetaIdentity 
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME = "meta.identity";
	
	private Map _versions;
	
	public MetaIdentity()
	{
		_versions = new HashMap();

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

	public void addVersion( MetaVersion version, int id )
	throws TypeException
	{
		_versions.put(version, new Integer(id));
	}
	
	public int getVersion( MetaVersion version )
	{
		Integer value = (Integer) _versions.get(version);
		if (value == null ) return TypeLibrary.NOTYPE;
		return value.intValue();
	}
	
	public Set getVersions()
	{
		return _versions.keySet();
	}
	
	// NOTE may not be needed.
	public static class MetaNameTypeWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object o ) 
		throws TypeException, IOException
		{
			MetaIdentity mn = (MetaIdentity) o;
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }



}
