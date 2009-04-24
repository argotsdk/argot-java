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

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeWriter;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEnvelop;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class ModelWriterAuto
implements TypeBound, TypeLibraryWriter
{
	private MetaExpressionResolver _expressionResolver;
	private MetaExpression _metaExpression;
	
	public ModelWriterAuto( MetaExpressionResolver resolver )
	{
		_expressionResolver = resolver;
		_metaExpression = null;
	}
	
	public ModelWriterAuto()
	{
		this( getModelResolver() );
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		_metaExpression = (MetaExpression) definition;
	}
	
	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return _expressionResolver.getExpressionWriter(map, (MetaExpression) _metaExpression);
	}

	public static MetaExpressionResolver getModelResolver()
	{
		MetaExpressionModelResolver resolver = new MetaExpressionModelResolver();
		resolver.addExpressionMap(MetaSequence.class, new SequenceData.SequenceDataExpressionReader(), new SequenceData.SequenceDataExpressionWriter());
		resolver.addExpressionMap(MetaReference.class, new ReferenceData.ReferenceDataExpressionReader(), new ReferenceData.ReferenceDataExpressionWriter());
		resolver.addExpressionMap(MetaArray.class, new ArrayData.ArrayDataExpressionReader(), new ArrayData.ArrayDataExpressionWriter());
		resolver.addExpressionMap(MetaEnvelop.class, new EnvelopData.EnvelopDataExpressionReader(), new EnvelopData.EnvelopDataExpressionWriter());
		resolver.addExpressionMap(MetaAbstract.class, new AbstractData.AbstractDataExpressionReader(), new AbstractData.AbstractDataExpressionWriter());
		resolver.addExpressionMap(MetaTag.class, new TagData.TagDataExpressionReader(), new TagData.TagDataExpressionWriter());
		return resolver;
	}
}

