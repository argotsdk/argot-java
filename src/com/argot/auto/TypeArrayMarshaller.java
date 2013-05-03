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
package com.argot.auto;

import java.io.IOException;
import java.lang.reflect.Array;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;

public class TypeArrayMarshaller
implements TypeLibraryWriter, TypeLibraryReader, TypeBound
{	
	private MetaExpressionResolver _resolver;
	private Class<?> _typeClass;
	private MetaExpression _sizeExpression;
	private MetaExpression _dataExpression;
	
	public TypeArrayMarshaller()
	{
		_resolver = new MetaExpressionLibraryResolver();
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		if ( !(definition instanceof MetaSequence) )
		{
			throw new TypeException("TypeArrayMarshaller: array not surrounded by sequence.");
		}
		
		MetaSequence sequence = (MetaSequence) definition;
		
		MetaExpression expression = sequence.getElement(0);
		if ( !(expression instanceof MetaArray))
		{
			throw new TypeException("TypeArrayMarshaller: not an array instance");			
		}
		
		MetaArray array = (MetaArray) expression;
		_sizeExpression = array.getSizeExpression();
		_dataExpression = array.getTypeExpression();
		Class<?> arrayClass = library.getClass(definitionId);
		if (!arrayClass.isArray())
		{
			throw new TypeException("TypeArrayMarshaller: not bound to array data type");
		}
		_typeClass = arrayClass.getComponentType();
	}
	
	private static class TypeArrayMarshallerReader
	implements TypeReader
	{
		private Class<?> _typeClass;
		private TypeReader _size;
		private TypeReader _data;
		
		public TypeArrayMarshallerReader( Class<?> typeClass, TypeReader size, TypeReader data )
		{
			_typeClass = typeClass;
			_size = size;
			_data = data;
		}
		
	    public Object read(TypeInputStream in)
	    throws TypeException, IOException
	    {
	    	Object size = _size.read(in);
	    	int s=0;
	    	if ( size instanceof Byte )
	    		s = ((Byte)size).intValue();
	    	else if (size instanceof Short )
	    		s = ((Short)size).intValue();
	    	else if (size instanceof Integer )
	    		s = ((Integer)size).intValue();
	    	else if (size instanceof Long )
	    		s = ((Long)size).intValue();
	    	else
	    	{
	    		if ( size != null)
	    			throw new TypeException("TypeArrayMarshaller: Size type not an integer type: " + size.getClass().getName() );
	    		throw new TypeException("TypeArrayMarshaller: Size returned null value");
	    	}
	    	
	    	Object array = Array.newInstance(_typeClass, s);
	    	for(int x=0; x<s; x++)
	    	{
	    		Array.set(array, x, _data.read(in));
	    	}
	    	return array;
	    }
	}

	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return new TypeArrayMarshallerReader( _typeClass, _resolver.getExpressionReader(map, _sizeExpression), _resolver.getExpressionReader(map,_dataExpression));
	}
	
	
	private class TypeArrayMarshallerWriter
	implements TypeWriter
	{
		private TypeWriter _size;
		private TypeWriter _data;
		
		public TypeArrayMarshallerWriter(TypeWriter size, TypeWriter data)
		{
			_size = size;
			_data = data;
		}

		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if (o == null)
				throw new TypeException("TypeArrayMarshaller: Object value is null");
			
	 		if ( !o.getClass().isArray() )
	 		{
	 			throw new TypeException("TypeArrayMarshaller: Object value is not an array type");	
	 		}
	 		
	 		int length = Array.getLength(o);
	 		_size.write(out, new Integer(length));
	 		
	 		for(int x=0; x< length; x++)
	 		{
	 			_data.write(out, Array.get(o, x));
	 		}
		}
	}

	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return new TypeArrayMarshallerWriter(_resolver.getExpressionWriter(map, _sizeExpression), _resolver.getExpressionWriter(map, _dataExpression));
	}
	
}
