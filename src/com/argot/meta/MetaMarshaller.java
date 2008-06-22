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
package com.argot.meta;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;


public class MetaMarshaller
implements TypeBound,TypeLibraryReader, TypeLibraryWriter
{
	private MetaExpression _expression;
	private MetaExpressionResolver _resolver;
	
	public MetaMarshaller()
	{
		_resolver = new MetaExpressionLibraryResolver();
	}
	public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
	throws TypeException 
	{
		_expression = (MetaExpression) definition;
	}

	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return _resolver.getExpressionReader(map, _expression);
	}

	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return _resolver.getExpressionWriter(map, _expression);
	}
 
}
