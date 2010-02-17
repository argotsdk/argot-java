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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;
import com.argot.meta.MetaTag;

public class TagData 
extends ModelData
{
	private MetaTag _metaTag;
	private Object _data;
	
	public TagData( MetaTag metaTag, Object data )
	{
		_metaTag = metaTag;
		_data = data;
	}
	
	public Object getData() 
	{
		return _data;
	}

	public MetaExpression getStructure() 
	{
		return _metaTag;
	}
	
	public static class TagDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return new TagDataReader(metaTag, resolver.getExpressionReader(map, metaTag.getExpression()));		
		}	
		
	}
	
	private static class TagDataReader
	implements TypeReader
	{
		private MetaTag _metaTag;
		private TypeReader _expression;
		
		public TagDataReader(MetaTag metaTag, TypeReader expression)
		{
			_metaTag = metaTag;
			_expression = expression;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
	        return new TagData( _metaTag,_expression.read( in ));
		}		
	}

	public static class TagDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return new TagDataWriter(resolver.getExpressionWriter(map, metaTag.getExpression()));		
		}
	}
	
	private static class TagDataWriter
	implements TypeWriter
	{
		private TypeWriter _writer;
		
		public TagDataWriter(TypeWriter type )
		{
			_writer = type;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			TagData data = (TagData) o;
			_writer.write(out, data.getData());
		}
		
	}
	
	
	
}
