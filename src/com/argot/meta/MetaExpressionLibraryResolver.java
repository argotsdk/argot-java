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
		// The expression.getTypeId() returns meta identity(un-versioned identifier) of
		// the expression type. Using the map to return the stream identifier will get
		// the mapped specific version of the identifier.
		
		// TODO Needs to be fixed.  This should get the expressionReader from somewhere
		// else.  Going via the map requires all meta types to be mapped which is not ideal.
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
		//TypeLibraryWriter writer = map.getLibrary().getWriter(expression.getTypeId());
		if(!(writer instanceof MetaExpressionWriter))
		{
			throw new TypeException("MetaExpressionWriter expected. Found: " + writer.getClass().getName() );
		}
		MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
		return expressionWriter.getExpressionWriter(map,this, expression);    	
	}
}
