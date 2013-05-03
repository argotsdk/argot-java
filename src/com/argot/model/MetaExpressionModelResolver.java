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

import java.util.HashMap;
import java.util.Map;

import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionWriter;

public class MetaExpressionModelResolver 
extends MetaExpressionLibraryResolver
{
	private Map<Class<?>,MetaExpressionReader> _readerMap;
	private Map<Class<?>,MetaExpressionWriter> _writerMap;
	
	public MetaExpressionModelResolver()
	{
		_readerMap = new HashMap<Class<?>,MetaExpressionReader>();
		_writerMap = new HashMap<Class<?>,MetaExpressionWriter>();
	}
	
	public void addExpressionMap( Class<?> clss, MetaExpressionReader reader, MetaExpressionWriter writer)
	{
		_readerMap.put(clss, reader);
		_writerMap.put(clss, writer);
	}

	private MetaExpressionReader getReader(Class<?> clss)
	{
		return  (MetaExpressionReader) _readerMap.get(clss);
	}
	
	private MetaExpressionWriter getWriter(Class<?> clss)
	{
		return  (MetaExpressionWriter) _writerMap.get(clss);
	}	

	public TypeReader getExpressionReader(TypeMap map, MetaExpression expression)
	throws TypeException 
	{
		MetaExpressionReader reader = getReader(expression.getClass());
		if ( reader == null )
		{
			return super.getExpressionReader(map, expression);
		}
		return reader.getExpressionReader(map, this, expression);
	}

	public TypeWriter getExpressionWriter(TypeMap map, MetaExpression expression)
	throws TypeException 
	{
		MetaExpressionWriter writer = getWriter(expression.getClass());
		if ( writer == null )
		{
			return super.getExpressionWriter(map, expression);
		}
		return writer.getExpressionWriter(map, this, expression);
	}

}
