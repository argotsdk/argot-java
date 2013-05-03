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

public class MetaIdentity 
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME = "meta.identity";
	
	private Map<MetaVersion,Integer> _versions;
	
	public MetaIdentity()
	{
		_versions = new HashMap<MetaVersion,Integer>();

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
	
	public Set<MetaVersion> getVersions()
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
			//MetaIdentity mn = (MetaIdentity) o;
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }



}
