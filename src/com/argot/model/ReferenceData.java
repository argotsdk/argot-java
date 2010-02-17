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
import com.argot.meta.MetaReference;

public class ReferenceData 
extends ModelData
{
	private MetaReference _reference;
	private Object _data;
	
	public ReferenceData( MetaReference reference, Object data )
	{
		_reference = reference;
		_data = data;
	}
	
	public MetaExpression getStructure() 
	{
		return _reference;
	}
	
	public MetaReference getReference()
	{
		return _reference;
	}
	
	public Object getData()
	{
		return _data;
	}
	
	public static class ReferenceDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataReader(metaReference, map.getReader( map.getStreamId(metaReference.getType())));
		}		
	}
	
	public static class ReferenceDataReader
	implements TypeReader
	{
		MetaReference _reference;
		TypeReader _reader;
		
		public ReferenceDataReader(MetaReference reference, TypeReader reader)
		{
			_reference = reference;
			_reader = reader;
		}
		
		public Object read(TypeInputStream in) 
		throws TypeException,IOException 
		{
			//return new ReferenceData( _reference, _reader.read(in));
			return _reader.read(in);
		}	
	}

	public static class ReferenceDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataWriter(map.getWriter( map.getStreamId(metaReference.getType())));		
		}
	}
	
	private static class ReferenceDataWriter
	implements TypeWriter
	{
		private TypeWriter _writer;
		
		public ReferenceDataWriter(TypeWriter type )
		{
			_writer = type;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if (!(o instanceof ReferenceData))
			{
				throw new TypeException("bad data");
			}
			ReferenceData data = (ReferenceData) o;
			//System.out.println("Reference Type:" + out.getTypeMap().getLibrary().getName( data.getReference().getType()) + " " + data.getData().getClass().getName() );
			_writer.write(out, data.getData());
		}
	}
}
