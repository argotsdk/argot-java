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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class TypeBeanMarshaller 
implements TypeLibraryReader, TypeLibraryWriter, TypeBound
{
	MetaExpressionResolver _expressionResolver;
	Class<?> _typeClass;
	Method[] _getMethods;
	Method[] _setMethods;	
	MetaSequence _sequence;

	public TypeBeanMarshaller()
	{
		this(new MetaExpressionLibraryResolver());
	}
	
	public TypeBeanMarshaller(MetaExpressionResolver resolver )
	{
		_expressionResolver = resolver;
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		if ( !(definition instanceof MetaSequence) )
		{
			throw new TypeException("TypeBeanMarshaller: Not an array instance");
		}
		
		_sequence = (MetaSequence) definition;
		_typeClass = library.getClass(definitionId);
		
		_getMethods = new Method[_sequence.size()];
		_setMethods = new Method[_sequence.size()];		
		for (int x=0;x<_sequence.size();x++)
		{
			MetaExpression expression = _sequence.getElement(x);
			if ( !(expression instanceof MetaTag))
			{
				throw new TypeException("TypeBeanMarshaller: All sequence elements not meta.tag type.");			
			}
			
			MetaTag reference = (MetaTag) expression;
			String description = reference.getDescription();
			String firstChar = description.substring(0,1);
			firstChar = firstChar.toUpperCase();
			String method = "get" + firstChar + description.substring(1);
			try 
			{
				Class<?>[] empty = new Class[0];
				_getMethods[x] = _typeClass.getDeclaredMethod(method, empty);
			} 
			catch (SecurityException e) 
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "."+ method,e);
			} 
			catch (NoSuchMethodException e) 
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "." + method,e);
			}

			method = "set" + firstChar + description.substring(1);
			_setMethods[x] = resolveSetMethod(_typeClass,method);
			
		}
	}
	
	/*
	 * Bean set methods need to take one argument.  The problem we face is that
	 * if the reference type is abstract it won't have any class.  This way we
	 * take the first method with the correct name and one argument.  We assume
	 * that the argument will match what we read off the wire.
	 */
	private Method resolveSetMethod( Class<?> typeClass, String method ) 
	throws TypeException
	{
		Method[] methods = typeClass.getMethods();
		for (int x=0;x<methods.length;x++)
		{
			if (methods[x].getName().equals(method))
			{
				if (methods[x].getParameterTypes().length == 1)
				{
					return methods[x];
				}
			}
		}
		
		throw new TypeException("TypeBeanMarshaller: No setter method found:" + _typeClass.getName() + "." + method);
	}

	
	private class TypeBeanMarshallerReader
	implements TypeReader
	{
		private TypeReader[] _sequenceReaders;
		
		public TypeBeanMarshallerReader(TypeReader[] sequenceReaders )
		{
			_sequenceReaders = sequenceReaders;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
				Object o;
				try 
				{
					o = _typeClass.newInstance();
				} 
				catch (InstantiationException e) 
				{
					throw new TypeException(e.getMessage(),e);
				} 
				catch (IllegalAccessException e) 
				{
					throw new TypeException(e.getMessage(),e);
				}
				
				
				Object[] args = new Object[1];
				for (int x=0;x<_sequenceReaders.length;x++)
				{
					args[0] = _sequenceReaders[x].read(in);

					try
					{
						_setMethods[x].invoke(o, args);
					} 
					catch (IllegalArgumentException e) 
					{
						throw new TypeException("TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName() + "(" + args[0].getClass().getName() + ")",e);
					} 
					catch (IllegalAccessException e) 
					{
						throw new TypeException("TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName() + "(" + args[0].getClass().getName() + ")",e);
					} 
					catch (InvocationTargetException e) 
					{
						throw new TypeException("TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName() + "(" + args[0].getClass().getName() + ")",e);
					}
					
				}
				return o;
			
		}
		
	}

	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		TypeReader readers[] = new TypeReader[_sequence.size()];
		
		for (int x=0;x<readers.length;x++)
		{
			readers[x] = _expressionResolver.getExpressionReader( map, _sequence.getElement(x) );
		}
		
		return new TypeBeanMarshallerReader( readers );
	}
	
	
	private class TypeBeanMarshallerWriter
	implements TypeWriter
	{
		TypeWriter[] _sequenceWriters;
		
		public TypeBeanMarshallerWriter(TypeWriter[] sequenceWriters )
		{
			_sequenceWriters = sequenceWriters;
		}

		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			for (int x=0;x<_sequenceWriters.length;x++)
			{
				try 
				{
					_sequenceWriters[x].write(out, _getMethods[x].invoke(o, (Object[]) null));
				} 
				catch (IllegalArgumentException e) 
				{
					throw new TypeException("TypeBeanMarshaller: Failed to get data from object:" + _typeClass.getName() + "." + _getMethods[x].getName() + "()",e);
				} 
				catch (IllegalAccessException e) 
				{
					throw new TypeException("TypeBeanMarshaller: Failed to get data from object:" + _typeClass.getName() + "." + _getMethods[x].getName() + "()",e);
				} 
				catch (InvocationTargetException e) 
				{
					throw new TypeException("TypeBeanMarshaller: Failed to get data from object:" + _typeClass.getName() + "." + _getMethods[x].getName() + "()",e);
				}
			}
			
		}
	}

	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		TypeWriter writers[] = new TypeWriter[_sequence.size()];
		
		for (int x=0;x<writers.length;x++)
		{
			writers[x] = _expressionResolver.getExpressionWriter(map, _sequence.getElement(x));
		}
		
		return new TypeBeanMarshallerWriter(writers);
	}



}
