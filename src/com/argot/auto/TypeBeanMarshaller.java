package com.argot.auto;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

import com.argot.meta.MetaExpression;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;

public class TypeBeanMarshaller 
implements TypeReader, TypeWriter, TypeBound
{
	Class _typeClass;
	Method[] _getMethods;
	Method[] _setMethods;	
	MetaSequence _sequence;

	public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
	throws TypeException 
	{
		if ( !(definition instanceof MetaSequence) )
		{
			throw new TypeException("TypeBeanMarshaller: Not an array instance");
		}
		
		_sequence = (MetaSequence) definition;
		_typeClass = library.getClass(typeId);
		
		_getMethods = new Method[_sequence.size()];
		_setMethods = new Method[_sequence.size()];		
		for (int x=0;x<_sequence.size();x++)
		{
			MetaExpression expression = _sequence.getElement(x);
			if ( !(expression instanceof MetaReference))
			{
				throw new TypeException("TypeBeanMarshaller: All sequence elements not meta.reference type.");			
			}
			
			MetaReference reference = (MetaReference) expression;
			String description = reference.getMethod();
			String firstChar = description.substring(0,1);
			firstChar = firstChar.toUpperCase();
			String method = "get" + firstChar + description.substring(1);
			try 
			{
				Class[] empty = new Class[0];
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
	private Method resolveSetMethod( Class typeClass, String method ) 
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
	
	public void write(TypeOutputStream out, Object o, TypeElement element)
	throws TypeException, IOException 
	{
		for (int x=0;x<_sequence.size();x++)
		{
			try 
			{
				_sequence.getElement(x).doWrite(out, _getMethods[x].invoke(o, null));
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

	public Object read(TypeInputStream in, TypeElement element)
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
			for (int x=0;x<_sequence.size();x++)
			{
				args[0] = _sequence.getElement(x).doRead(in);

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
