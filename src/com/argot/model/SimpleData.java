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
