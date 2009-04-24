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
package com.argot.model;

import java.io.IOException;

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
import com.argot.meta.MetaExpression;

public class SimpleData 
extends ModelData
{
	private int _typeId;
	private MetaExpression _definition;
	private Object _data;
	
	public SimpleData( int id, MetaExpression expression, Object data )
	{
		_typeId = id;
		_definition = expression;
		_data = data;
	}
	
	public Object getData() 
	{
		return _data;
	}
	
	public int getType()
	{
		return _typeId;
	}

	public MetaExpression getStructure() 
	{
		return _definition;
	}
	
	public static class SimpleDataExpressionReader
	implements TypeLibraryReader, TypeBound
	{
		int _id;
		MetaExpression _expression;

		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return new SimpleDataReader(_id,_expression, map.getLibrary().getReader(_id).getReader(map));
		}

		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_id = definitionId;
			_expression = (MetaExpression) definition;
		}
	}
	
	private static class SimpleDataReader
	implements TypeReader
	{
		private int _id;
		private MetaExpression _expression;
		private TypeReader _reader;
		
		public SimpleDataReader(int id, MetaExpression expression, TypeReader reader)
		{
			_id = id;
			_expression = expression;
			_reader = reader;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
	        return new SimpleData( _id, _expression,_reader.read( in ));
		}		
	}

	public static class SimpleDataExpressionWriter
	implements TypeLibraryWriter,TypeBound
	{
		int _id;
		MetaExpression _expression;

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return new SimpleDataWriter(map.getLibrary().getWriter(_id).getWriter(map));		
		}

		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_id = definitionId;
		}
	}
	
	private static class SimpleDataWriter
	implements TypeWriter
	{
		private TypeWriter _writer;
		
		public SimpleDataWriter(TypeWriter type )
		{
			_writer = type;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			SimpleData data = (SimpleData) o;
			_writer.write(out, data.getData());
		}
		
	}
	
	
	
}
