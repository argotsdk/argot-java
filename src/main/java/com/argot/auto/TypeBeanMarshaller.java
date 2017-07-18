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
import com.argot.TypeInstantiator;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReaderWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class TypeBeanMarshaller implements TypeLibraryReaderWriter, TypeBound
{
	MetaExpressionResolver _expressionResolver;
	TypeInstantiator _instantiator;
	Class<?> _typeClass;
	Method[] _getMethods;
	Method[] _setMethods;
	MethodHandleReader[] _methodHandleReaders;
	MethodHandleWriter[] _methodHandleWriters;

	MetaSequence _sequence;

	public TypeBeanMarshaller()
	{
		this(new MetaExpressionLibraryResolver(), null);
	}

	public TypeBeanMarshaller(final TypeInstantiator instantiator)
	{
		this(new MetaExpressionLibraryResolver(), instantiator);
	}

	public TypeBeanMarshaller(final MetaExpressionResolver resolver)
	{
		this(resolver, null);
	}

	public TypeBeanMarshaller(final MetaExpressionResolver resolver, final TypeInstantiator instantiator)
	{
		_expressionResolver = resolver;
		_instantiator = instantiator;
	}

	@Override
	public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition) throws TypeException
	{
		if (!(definition instanceof MetaSequence))
		{
			throw new TypeException("TypeBeanMarshaller: Not an array instance");
		}

		if (_instantiator == null)
		{
			_instantiator = library.getInstantiator(definitionId);
		}

		_sequence = (MetaSequence) definition;
		_typeClass = library.getClass(definitionId);

		_getMethods = new Method[_sequence.size()];
		_setMethods = new Method[_sequence.size()];
		_methodHandleReaders = new MethodHandleReader[_sequence.size()];
		_methodHandleWriters = new MethodHandleWriter[_sequence.size()];

		for (int x = 0; x < _sequence.size(); x++)
		{
			final MetaExpression expression = _sequence.getElement(x);
			if (!(expression instanceof MetaTag))
			{
				throw new TypeException("TypeBeanMarshaller: All sequence elements not meta.tag type.");
			}

			final MetaTag reference = (MetaTag) expression;
			final String description = reference.getDescription();
			final MetaExpression tagExpression = reference.getExpression();
			String referenceType = null;
			if (tagExpression instanceof MetaReference)
			{
				final MetaReference tagReference = (MetaReference) reference.getExpression();
				referenceType = library.getName(tagReference.getType()).getFullName();
			}
			String firstChar = description.substring(0, 1);
			firstChar = firstChar.toUpperCase();
			String method = "get" + firstChar + description.substring(1);

			try
			{
				final Class<?>[] empty = new Class[0];
				_getMethods[x] = _typeClass.getMethod(method, empty);
				_methodHandleWriters[x] = MethodHandleWriter.getWriter(_getMethods[x], referenceType, false);
			}
			catch (final SecurityException e)
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "." + method, e);
			}
			catch (final NoSuchMethodException e)
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "." + method, e);
			}
			catch (final IllegalAccessException e)
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "." + method, e);
			}

			method = "set" + firstChar + description.substring(1);
			_setMethods[x] = resolveSetMethod(_typeClass, method);
			try
			{
				_methodHandleReaders[x] = MethodHandleReader.getReader(_setMethods[x], referenceType);
			}
			catch (final IllegalAccessException e)
			{
				throw new TypeException("TypeBeanMarshaller: No getter method found:" + _typeClass.getName() + "." + method, e);
			}
		}
	}

	/*
	 * Bean set methods need to take one argument. The problem we face is that if the reference type is abstract it won't have any class. This way we take the first method with the correct name and one argument. We assume that the argument will match what we read off the wire.
	 */
	private Method resolveSetMethod(final Class<?> typeClass, final String method) throws TypeException
	{
		final Method[] methods = typeClass.getMethods();
		for (int x = 0; x < methods.length; x++)
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

	private class TypeBeanMarshallerReader implements TypeReader
	{
		private final TypeReader[] _sequenceReaders;

		public TypeBeanMarshallerReader(final TypeReader[] sequenceReaders)
		{
			_sequenceReaders = sequenceReaders;
		}

		@Override
		public Object read(final TypeInputStream in) throws TypeException, IOException
		{
			final Object o = _instantiator.newInstance();

			for (int x = 0; x < _sequenceReaders.length; x++)
			{
				if (_methodHandleReaders[x] != null)
				{
					try
					{
						_methodHandleReaders[x].read(o, in);
					}
					catch (final Throwable e)
					{
						throw new TypeException("TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName(), e);
					}
				}
				else
				{

					final Object arg = _sequenceReaders[x].read(in);

					try
					{
						_setMethods[x].invoke(o, arg);
					}
					catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException e)
					{
						throw new TypeException(
								"TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName() + "(" + arg.getClass().getName() + ")", e);
					}
					catch (final RuntimeException e)
					{
						throw new TypeException(
								"TypeBeanMarshaller: Failed to call set method:" + _typeClass.getName() + "." + _setMethods[x].getName() + "(" + arg.getClass().getName() + ")", e);
					}
				}

			}
			return o;

		}

	}

	@Override
	public TypeReader getReader(final TypeMap map) throws TypeException
	{
		final TypeReader readers[] = new TypeReader[_sequence.size()];

		for (int x = 0; x < readers.length; x++)
		{
			readers[x] = _expressionResolver.getExpressionReader(map, _sequence.getElement(x));
		}

		return new TypeBeanMarshallerReader(readers);
	}

	private class TypeBeanMarshallerWriter implements TypeWriter
	{
		TypeWriter[] _sequenceWriters;

		public TypeBeanMarshallerWriter(final TypeWriter[] sequenceWriters)
		{
			_sequenceWriters = sequenceWriters;
		}

		@Override
		public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException
		{
			for (int x = 0; x < _sequenceWriters.length; x++)
			{
				if (_methodHandleWriters[x] != null)
				{
					try
					{
						_methodHandleWriters[x].write(o, out);
					}
					catch (final Throwable e)
					{
						throw new TypeException("TypeBeanMarshaller: Failed to get data from object:" + _typeClass.getName() + "." + _getMethods[x].getName() + "()", e);
					}
				}
				else
				{
					try
					{
						_sequenceWriters[x].write(out, _getMethods[x].invoke(o, (Object[]) null));
					}
					catch (final IllegalArgumentException | IllegalAccessException | InvocationTargetException | TypeException e)
					{
						throw new TypeException("TypeBeanMarshaller: Failed to get data from object:" + _typeClass.getName() + "." + _getMethods[x].getName() + "()", e);
					}
				}

			}

		}
	}

	@Override
	public TypeWriter getWriter(final TypeMap map) throws TypeException
	{
		final TypeWriter writers[] = new TypeWriter[_sequence.size()];

		for (int x = 0; x < writers.length; x++)
		{
			writers[x] = _expressionResolver.getExpressionWriter(map, _sequence.getElement(x));
		}

		return new TypeBeanMarshallerWriter(writers);
	}

}
