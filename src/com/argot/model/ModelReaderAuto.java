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
