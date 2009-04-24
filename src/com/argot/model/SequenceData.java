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

import java.io.IOException;

import com.argot.TypeConstructor;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;
import com.argot.meta.MetaSequence;

public class SequenceData 
extends ModelData
{
	private MetaSequence _sequence;
	private Object[] _data;
	
	public SequenceData(TypeElement sequence, Object[] data)
	{
		_sequence = (MetaSequence) sequence;
		_data = data;
	}
	
	public MetaExpression getStructure()
	{
		return _sequence;
	}
	
	public MetaSequence getMetaSequence()
	{
		return _sequence;
	}
	
	public Object getData()
	{
		return _data;
	}
	
	public Object[] getSequenceData()
	{
		return _data;
	}
	
	public static class SequenceDataConstructor
	implements TypeConstructor
	{
		public Object construct(TypeElement sequence, Object[] parameters)
		throws TypeException 
		{
			return new SequenceData(sequence,parameters);
		}
	}
	
	public static class SequenceDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaSequence sequence = (MetaSequence) element;
			
			TypeReader[] readers = new TypeReader[ sequence.size() ];
			for ( int x=0; x < sequence.size() ; x++ )
			{	
				//readers[x] = sequence.getElementReader(map,sequence.getElement(x));
				readers[x] = resolver.getExpressionReader(map, sequence.getElement(x));
			}		
			return new SequenceDataReader(sequence, readers);
		}
	}

	private static class SequenceDataReader
	implements TypeReader
	{
		private MetaSequence _sequence;
		private TypeReader[] _readers;
		
		public SequenceDataReader( MetaSequence sequence, TypeReader[] readers )
		{
			_sequence = sequence;
			_readers = readers;
		}

		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			Object[] objects = new Object[ _readers.length ];
			
			for ( int x=0; x < _readers.length ; x++ )
			{
				
				objects[x] = _readers[x].read(in);
			}			
			return new SequenceData(_sequence,objects);
		}
		
	}
	
	public static class SequenceDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaSequence sequence = (MetaSequence) element;
			
			TypeWriter[] writers = new TypeWriter[ sequence.size() ];
			for ( int x=0; x < sequence.size() ; x++ )
			{	
				writers[x] = resolver.getExpressionWriter(map, sequence.getElement(x));
			}
			
			return new SequenceDataWriter(writers);
		}
	}
	
	
	private static class SequenceDataWriter
	implements TypeWriter
	{
		private TypeWriter[] _writers;
		
		public SequenceDataWriter(TypeWriter[] readers )
		{
			_writers = readers;
		}

		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			SequenceData data = (SequenceData)o;
			Object[] sequenceData = data.getSequenceData();
			for ( int x=0; x < _writers.length ; x++ )
			{
				_writers[x].write(out,sequenceData[x]);
			}			
		}
	}
}
