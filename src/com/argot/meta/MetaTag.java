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
		if ( description == null ) throw new IllegalArgumentException("MetaTag: description is null");
		if ( expression == null ) throw new IllegalArgumentException("MetaTag: expression is null");
		
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
