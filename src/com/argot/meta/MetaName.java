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
import java.util.ArrayList;
import java.util.List;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt8;

public class MetaName 
{
	public static final String TYPENAME = "meta.name";
	public static final String VERSION = "1.3";

	private String[] _name;
	
	public MetaName( String[] name )
	{
		_name = name;
	}
	
	public String[] getParts()
	{
		return (String[]) _name.clone();
	}
	
	public int size()
	{
		return _name.length;
	}
	
	
	
	public String toString()
	{
		StringBuffer buffer = new StringBuffer();
		
		for (int x=0; x< _name.length-1; x++ )
		{
			buffer.append(_name[x]);
			buffer.append(".");
		}
		
		buffer.append(_name[_name.length-1]);
		
		return buffer.toString();
	}
	
	public static MetaName parseName(String name)
	throws TypeException
	{
		if ( name == null )
			throw new TypeException("Unable to parse name");
		
		String nameLeft = name;
		int index = -1;
		List names = new ArrayList();
		while( (index = nameLeft.indexOf(".")) != -1 )
		{
			String nextStr = nameLeft.substring(0,index);
			if ( nextStr == null || nextStr.length() == 0 )
				throw new TypeException( "Invalid name:" + name );
			
			nameLeft = nameLeft.substring(index+1);
			names.add(nextStr);
		}
		
		String lastStr = nameLeft.substring(index+1);
		names.add(lastStr);
		
		
		String[] finalArray = new String[names.size()];
		finalArray = (String[]) names.toArray(finalArray);
		return new MetaName(finalArray);
		
	}

	public static class MetaNameTypeWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object obj ) 
		throws TypeException, IOException
		{
			MetaName mn = (MetaName) obj;
			String[] nameParts = mn.getParts();
			
			out.writeObject(  UInt8.TYPENAME, new Integer( nameParts.length ));
	
			for ( int x=0 ; x < nameParts.length ; x++ )
			{
				out.writeObject( "u8utf8", nameParts[x] );
			}
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
			String[] name = new String[object.length];
			for (int x=0; x<object.length;x++)
			{
				name[x] = (String) object[x];
			}
			return new MetaName(name);
		}
		
	}
}
