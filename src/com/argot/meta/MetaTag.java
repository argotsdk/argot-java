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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.U8Ascii;
import com.argot.common.U8Utf8;

public class MetaTag
extends MetaExpression
{
	public static final String TYPENAME = "meta.tag";
	public static final String VERSION = "1.3";

	private String _description;
	private MetaExpression _expression;
	
	public MetaTag( String description, MetaExpression expression )
	{
		_description = description;
		_expression = expression;
	}
	
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException
    {
        super.bind(library, definitionId, location, definition);
        _expression.bind(library, definitionId, location, definition);
    }
    
	public String getTypeName() {
		return TYPENAME;
	}

	public String getDescription()
	{
		return _description;
	}
	
	public MetaExpression getExpression()
	{
		return _expression;
	}
	
	public static class MetaTagTypeReader
	extends MetaExpressionReaderAuto
	implements MetaExpressionReader
	{
		public MetaTagTypeReader() 
		{
			super(MetaTag.class);
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return resolver.getExpressionReader(map, metaTag._expression);
		}	
	}
	
	public static class MetaTagTypeWriter 
	implements TypeLibraryWriter, TypeWriter, MetaExpressionWriter
	{
		public void write(TypeOutputStream out, Object obj ) 
		throws TypeException, IOException
		{
			MetaTag tag = (MetaTag) obj;
	
			out.writeObject(  U8Utf8.TYPENAME, tag._description);
			out.writeObject(  MetaExpression.TYPENAME, tag._expression );
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaTag metaTag = (MetaTag) element;
			return resolver.getExpressionWriter(map, metaTag._expression);
		}

	}

	
}
