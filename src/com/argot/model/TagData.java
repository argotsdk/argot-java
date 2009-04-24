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
