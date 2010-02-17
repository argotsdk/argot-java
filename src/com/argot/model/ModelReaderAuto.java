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

import java.util.Iterator;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class ModelReaderAuto
implements TypeBound,TypeLibraryReader
{
	private MetaExpressionResolver _expressionResolver;
	private MetaExpression _metaExpression;
	
	public ModelReaderAuto( MetaExpressionResolver resolver )
	{
		_expressionResolver = resolver;
		_metaExpression = null;
	}
	
	public ModelReaderAuto()
	{
		this( getModelResolver() );
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		_metaExpression = (MetaExpression) definition;
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return _expressionResolver.getExpressionReader(map, (MetaExpression) _metaExpression);
	}

	public static MetaExpressionResolver getModelResolver()
	{
		MetaExpressionModelResolver resolver = new MetaExpressionModelResolver();
		resolver.addExpressionMap(MetaSequence.class, new SequenceData.SequenceDataExpressionReader(), null);
		resolver.addExpressionMap(MetaReference.class, new ReferenceData.ReferenceDataExpressionReader(), null);
		resolver.addExpressionMap(MetaArray.class, new ArrayData.ArrayDataExpressionReader(), null);
		resolver.addExpressionMap(MetaEnvelope.class, new EnvelopData.EnvelopDataExpressionReader(), null);
		resolver.addExpressionMap(MetaAbstract.class, new AbstractData.AbstractDataExpressionReader(), null);
		resolver.addExpressionMap(MetaTag.class, new TagData.TagDataExpressionReader(), null);
		return resolver;
	}
	
	public static void configureTypeMap( TypeMap map )
	throws TypeException
	{
		Iterator iter = map.getIdList().iterator();
		while (iter.hasNext())
		{
			int id = ((Integer) iter.next()).intValue();
			if (!map.isSimpleType(id))
			{
				map.setReader(id, new ModelReaderAuto() ); 
				map.setWriter(id, new ModelWriterAuto() );	
			}
			else
			{
				map.setReader(id, new SimpleData.SimpleDataExpressionReader() );
				map.setWriter(id, new SimpleData.SimpleDataExpressionWriter() );				
			}
		}		
	}
}
