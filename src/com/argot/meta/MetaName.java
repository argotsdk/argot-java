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
import java.util.ArrayList;
import java.util.List;

import com.argot.ReferenceTypeMap;
import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeLocation;
import com.argot.TypeLocationBase;
import com.argot.TypeLocationName;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.U8Utf8;
import com.argot.common.UInt8;

public class MetaName 
{
	public static final String TYPENAME = "meta.name";
	public static final String VERSION = "1.3";

	private int _group;
	private String _fullName;
	private String _name;
	
	public MetaName( int group, String fullName, String name )
	{
		_group = group;
		_fullName = fullName;
		_name = name;
	}
	
	public int getGroup()
	{
		return _group;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public String getFullName()
	{
		return _fullName;
	}
	
	public String toString()
	{
		throw new RuntimeException("Don't use this");
	}
	
	
	public static MetaName parseName(TypeLibrary library, String name)
	throws TypeException
	{
		if ( library == null ) throw new IllegalArgumentException("DictionaryName: library is null");
		if ( name == null ) throw new IllegalArgumentException("DictionaryName: name is null");

		int index = name.lastIndexOf(".");
		if ( index > -1 )
		{
			String group = name.substring(0,index);
			int groupId = library.getTypeId(group);
			return new MetaName(groupId,name, name.substring(index+1));
		}
		else
		{
			return new MetaName(0,name,name);
		}
		
	}
	
	
	public static class MetaNameTypeWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object obj ) 
		throws TypeException, IOException
		{
			MetaName mn = (MetaName) obj;
			//String[] nameParts = mn.getParts();
			
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			int streamId = mapCore.referenceMap().getStreamId(mn.getGroup());
			out.writeObject( "meta.id", new Integer(streamId) );
			out.writeObject(  U8Utf8.TYPENAME, mn.getName() );
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
	}

	public static class MetaNameTypeLibraryReader
	implements TypeLibraryReader, TypeBound
	{
		MetaMarshaller _marshaller = new MetaMarshaller();

		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return new MetaNameTypeReader( _marshaller.getReader(map));
		}

		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_marshaller.bind(library, definitionId, definition);
		}
	}
	

	public static class MetaNameTypeReader
	implements TypeReader
	{
		TypeReader _reader;

		public MetaNameTypeReader(TypeReader reader) 
		{
			_reader = reader;
		}

		public Object read(TypeInputStream in) 
		throws TypeException, IOException 
		{
			Object[] object = (Object[]) _reader.read(in);
			Integer groupId = (Integer) object[0];
			String name = (String) object[1];

			ReferenceTypeMap mapCore = (ReferenceTypeMap) in.getTypeMap();
			
			int defId = mapCore.referenceMap().getDefinitionId(groupId.intValue());
			
			TypeLocation location = mapCore.referenceMap().getLocation(groupId.intValue());
			if (location instanceof TypeLocationName) 
			{
				TypeLocationName locName = (TypeLocationName) location;
				String fullName = locName.getName().getFullName();				
				return new MetaName(defId,fullName+"."+name,name);
			}
			else if (location instanceof TypeLocationBase)
			{
				return new MetaName(defId, name, name );
			}
			else
			{
				throw new TypeException("Name group id not a group");				
			}
		}
		
	}

}
