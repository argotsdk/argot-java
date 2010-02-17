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
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class ArrayData 
extends ModelData
{
	private MetaArray _array;
	private Object _size;
	private Object[] _data;
	
	public ArrayData( MetaArray array, Object size, Object[] data )
	{
		_array = array;
		_size = size;
		_data = data;
	}
	
	public MetaExpression getStructure() 
	{
		return _array;
	}
	
	public MetaArray getMetaArray()
	{
		return _array;
	}
	
	public Object getSizeData()
	{
		return _size;
	}
	
	public Object getData()
	{
		return _data;
	}
	
	public Object[] getArrayData()
	{
		return _data;
	}
	
	public static class ArrayDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaArray metaArray = (MetaArray) element;
			return new MetaArrayReader( metaArray, resolver.getExpressionReader(map, metaArray.getSizeExpression()), resolver.getExpressionReader(map, metaArray.getTypeExpression()));
		}	
	}
	
	private static class MetaArrayReader
	implements TypeReader
	{
		MetaArray _metaArray;
		TypeReader _size;
		TypeReader _data;
		
		private MetaArrayReader(MetaArray array, TypeReader size, TypeReader data)
		{
			_metaArray = array;
			_size = size;
			_data = data;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
		    Object sizeObject =  _size.read(in);
		    if (sizeObject instanceof ModelData)
		    {
		    	sizeObject = ((ModelData)sizeObject).getData();
		    }
		    if (sizeObject instanceof SimpleData)
		    {
		    	sizeObject = ((SimpleData)sizeObject).getData();
		    }
		    
			int size = 0;
			
			if ( sizeObject instanceof Byte )
			{
				size = ((Byte)sizeObject).intValue();
			}
			else if (sizeObject instanceof Short)
			{
				size = ((Short)sizeObject).intValue();
			}
			else if (sizeObject instanceof Integer )
			{
				size = ((Integer)sizeObject).intValue();
			}
			else if (sizeObject instanceof Long )
			{
			    size = ((Long)sizeObject).intValue();
			}
			else
			{
				throw new TypeException("MetaArray not able to use size object");
			}
					
			Object[] objects = new Object[ size ];
			for ( int x = 0 ; x < size; x ++ )
			{
				objects[x] = _data.read( in );
			}
			
			return new ArrayData( _metaArray, sizeObject, objects);
		}
		
	}
	
	public static class ArrayDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaArray metaArray = (MetaArray) element;
			return new ArrayDataWriter(metaArray, getSizeWriter(map, resolver, metaArray.getSizeExpression()), resolver.getExpressionWriter(map, metaArray.getTypeExpression()));		
		}
		
		private TypeWriter getSizeWriter(TypeMap map,MetaExpressionResolver resolver, MetaExpression expression) 
		throws TypeException
		{
	    	TypeLibraryWriter writer = map.getLibrary().getWriter(expression.getTypeId());
			if(!(writer instanceof MetaExpressionWriter))
			{
				throw new TypeException("MetaExpressionReader expected. Found: " + writer.getClass().getName() );
			}
			MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
			return expressionWriter.getExpressionWriter(map, resolver, expression);    	
		}
	}	
	
	
	private static class ArrayDataWriter
	implements TypeWriter
	{
		private TypeWriter _size;
		private TypeWriter _data;
		
		public ArrayDataWriter(MetaArray metaArray, TypeWriter size, TypeWriter data)
		{
			_size = size;
			_data = data;
		}

		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			ArrayData arrayData = (ArrayData) o;
			Object[] data = arrayData.getArrayData();
	 		
	 		_size.write(out, new Integer(data.length));
	 		
	 		for(int x=0; x< data.length; x++)
	 		{
	 			_data.write(out, data[x]);
	 		}
		}
	}
	
}
