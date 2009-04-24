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
	private Map _readerMap;
	private Map _writerMap;
	
	public MetaExpressionModelResolver()
	{
		_readerMap = new HashMap();
		_writerMap = new HashMap();
	}
	
	public void addExpressionMap( Class clss, MetaExpressionReader reader, MetaExpressionWriter writer)
	{
		_readerMap.put(clss, reader);
		_writerMap.put(clss, writer);
	}

	private MetaExpressionReader getReader(Class clss)
	{
		return  (MetaExpressionReader) _readerMap.get(clss);
	}
	
	private MetaExpressionWriter getWriter(Class clss)
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
