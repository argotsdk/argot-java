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

import com.argot.TypeException;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaExpressionLibraryResolver 
implements MetaExpressionResolver
{
	public TypeReader getExpressionReader( TypeMap map, MetaExpression expression ) 
	throws TypeException
	{
		int id = map.getStreamId(expression.getTypeId());
		
    	TypeLibraryReader reader = map.getLibrary().getReader(map.getDefinitionId(id));
		if(!(reader instanceof MetaExpressionReader))
		{
			throw new TypeException("MetaExpressionReader expected. Found: " + reader.getClass().getName() );
		}
		MetaExpressionReader expressionReader = (MetaExpressionReader) reader;
		return expressionReader.getExpressionReader(map,this, expression);    	
	}

	public TypeWriter getExpressionWriter(TypeMap map, MetaExpression expression)
	throws TypeException
	{
		int id = map.getStreamId(expression.getTypeId());
		TypeLibraryWriter writer = map.getLibrary().getWriter(map.getDefinitionId(id));
		if(!(writer instanceof MetaExpressionWriter))
		{
			throw new TypeException("MetaExpressionWriter expected. Found: " + writer.getClass().getName() );
		}
		MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
		return expressionWriter.getExpressionWriter(map,this, expression);    	
	}
}
