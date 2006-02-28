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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;
import com.argot.common.BigEndianUnsignedShort;
import com.argot.common.U8Ascii;
import com.argot.meta.MetaBase;

public class MetaParameter
extends MetaBase
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
	implements TypeReader
	{
		public Object read(TypeInputStream in, TypeElement element) 
		throws TypeException, IOException 
		{
			TypeReader reader = new TypeReaderAuto( MetaParameter.class );
			MetaParameter mp = (MetaParameter) reader.read( in, element );
			mp._typeId = in.getTypeMap().getSystemId( mp._typeId );
			return mp;
		}
	}
	
	public static class MetaParameterWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o, TypeElement element) 
		throws TypeException, IOException 
		{
			MetaParameter mp = (MetaParameter) o;
			int id = out.getTypeMap().getId( mp._typeId );
			out.writeObject( BigEndianUnsignedShort.TYPENAME, new Integer(id));
			out.writeObject( U8Ascii.TYPENAME, mp._name );
		}
	}
}
