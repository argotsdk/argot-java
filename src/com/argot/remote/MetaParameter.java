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
package com.argot.remote;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;
import com.argot.common.U8Ascii;
import com.argot.common.UInt16;
import com.argot.meta.MetaExpression;

public class MetaParameter
extends MetaExpression
{
	public static final String TYPENAME = "remote.parameter";

	private int _typeId;
	private String _name;
	
	public MetaParameter( int id, String name )
	{
		_typeId = id;
		_name = name;
	}
	
	public String getTypeName() 
	{
		return TYPENAME;
	}

	public int getParamType() 
	{
		return _typeId;
	}
	
	public String getParamName()
	{
		return _name;
	}

	public static class MetaParameterReader
	implements TypeReader,TypeBound
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaParameter.class );
		
		public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
		throws TypeException 
		{
			_reader.bind(library, definition, typeName, typeId);
		}
		
		public Object read(TypeInputStream in) 
		throws TypeException, IOException 
		{
			TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaParameter mp = (MetaParameter) reader.read( in );
			mp._typeId = in.getTypeMap().getSystemId( mp._typeId );
			return mp;
		}
	}
	
	public static class MetaParameterWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MetaParameter mp = (MetaParameter) o;
			int id = out.getTypeMap().getId( mp._typeId );
			out.writeObject( UInt16.TYPENAME, new Integer(id));
			out.writeObject( U8Ascii.TYPENAME, mp._name );
		}
	}
}
