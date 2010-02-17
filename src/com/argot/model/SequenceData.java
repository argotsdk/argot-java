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
