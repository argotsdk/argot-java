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
package com.argot.auto;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeConstructor;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;


public class TypeReaderAuto
implements TypeBound,TypeLibraryReader
{
	private MetaExpressionResolver _resolver;
	private TypeConstructor _constructor;
	private MetaSequence _metaSequence;
	
	public TypeReaderAuto( Class clss )
	{
		_resolver = new MetaExpressionLibraryResolver();
		_constructor = new TypeConstructorAuto( clss );
		_metaSequence = null;
	}
	
	public TypeReaderAuto( TypeConstructor constructor )
	{
		_resolver = new MetaExpressionLibraryResolver();
		_constructor = constructor;
	}

	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		if ( !( definition instanceof MetaSequence))
		{
			if (!(definition instanceof MetaSequence))
			{
				throw new TypeException( "TypeReaderAuto: required MetaSequence to read data." );
			}

			_metaSequence = (MetaSequence) definition;
			return;
		}
		_metaSequence = (MetaSequence) definition;
	}	
	
	private class TypeAutoReader
	implements TypeReader
	{
		private TypeReader _sequence;
		private MetaSequence _metaSequence;
		
		public TypeAutoReader( TypeReader sequence, MetaSequence metaSequence )
		{
			_sequence = sequence;
			_metaSequence = metaSequence;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException
		{
			Object[] objects = (Object[]) _sequence.read( in );
			return _constructor.construct( _metaSequence, objects );									
		}
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return new TypeAutoReader( _resolver.getExpressionReader(map, _metaSequence), _metaSequence );
	}



}
