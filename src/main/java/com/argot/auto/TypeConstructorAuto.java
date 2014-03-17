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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import com.argot.TypeConstructor;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeStreamException;

public class TypeConstructorAuto 
implements TypeConstructor
{
	private Class<?> _clss;
	private Constructor<?> _constructor;
	
	public TypeConstructorAuto( Class<?> clss )
	{
		_clss = clss;
		_constructor = null;
	}

    @SuppressWarnings("unchecked")
	public Object construct(TypeElement sequence, Object[] objects)
    throws TypeException
    {
    	if (_constructor == null)
    	{
    		resolveConstructor(objects);
    	}
    	
		try
		{
			Object[] arguments = new Object[objects.length];
			
			// Convert object[] to their parameter[] arguments.
			@SuppressWarnings("rawtypes")
			Class[] paramTypes = _constructor.getParameterTypes();
			for (int x=0;x<objects.length;x++)
			{
				if (paramTypes[x].isArray() && objects[x].getClass().isArray())
				{
					Object[] data = (Object[]) objects[x];
					arguments[x] = Arrays.copyOf(data,data.length,paramTypes[x] );
				}
				else
				{
					arguments[x] = objects[x];
				}
			}
			return _constructor.newInstance( arguments );
		}
		catch (IllegalArgumentException e)
		{
			//e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + _clss.getName() + ":" + e.toString() + ":" + createError(objects), e );						
		}
		catch (InstantiationException e)
		{
			//e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + _clss.getName() + ":" + e.toString(), e );
		}
		catch (IllegalAccessException e)
		{
			//e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + _clss.getName() + ":" + e.toString(), e );
		}
		catch (InvocationTargetException e)
		{
			//e.printStackTrace();
				
			throw new TypeException( "TypeReaderAuto:" + _clss.getName() + ":" + e.toString() + ":" + e.getCause().getMessage(),e );
		}
		catch (ClassCastException e)
		{
			throw new TypeException( "TypeReaderAuto:" + _clss.getName() + ":" + e.toString(),e );
		}
		
		
    }

    private void resolveConstructor(Object[] objects)
    throws TypeException
    {    
		Constructor<?> constructors[] = _clss.getConstructors();
		
		// Loop through all the constructors on the class.
		for ( int x=0; x < constructors.length; x++ )
		{
			// First check if the parameter lengths match.
			if (  constructors[x].getParameterTypes().length != objects.length )
				continue;
			
			// Assume the parameters are the same until we find a parameter that doesn't match.
			boolean found = true;
			Class<?>[] paramTypes = constructors[x].getParameterTypes();
			for ( int y=0; y < paramTypes.length; y++ )
			{
				// If a parameter is null we must assume that this constructor is ok.
				if ( objects[y] == null )
					continue;
				
				if ( paramTypes[y].isInstance( objects[y] ) )
					continue;

				// Basic comparison failed..  do some more rigourous checking.
				if ( objects[y] == null )
					continue;
						
				// First check if we have any basic types.
				if ( paramTypes[y].getName().equals( "short") && objects[y].getClass().getName().equals( "java.lang.Short") )
					continue;
											
				if ( paramTypes[y].getName().equals( "byte" ) && objects[y].getClass().getName().equals( "java.lang.Byte"))
					continue;
					
				if ( paramTypes[y].getName().equals( "int" ) && objects[y].getClass().getName().equals( "java.lang.Integer" ))
					continue;
						
				if ( paramTypes[y].getName().equals( "long" ) && objects[y].getClass().getName().equals( "java.lang.Long" ))
					continue;
						
				if ( paramTypes[y].getName().equals( "boolean") && objects[y].getClass().getName().equals( "java.lang.Boolean" ))
					continue;
				
				if ( paramTypes[y].getName().equals( "float" ) && objects[y].getClass().getName().equals( "java.lang.Float" ))
					continue;

				if ( paramTypes[y].getName().equals( "double" ) && objects[y].getClass().getName().equals( "java.lang.Double" ))
					continue;
				
				// If the parameter is an array see if all the objects in the array conform.
				if (paramTypes[y].isArray() && objects[y].getClass().isArray())
				{
					@SuppressWarnings("rawtypes")
					Class arrayType = objects[y].getClass().getComponentType();
					Object[] data = (Object[]) objects[y];
					boolean ok = true;
					for (int z=0;z<data.length;z++)
					{
						if (data[z] != null && !arrayType.isInstance(data[z]))
						{
							ok = false;
						}
					}
					if (ok)
					{
						continue;
					}
				}
				
				// We couldn't find a match between this parameter and object value.
				// This constructor isn't a match.
				found = false;
				break;
			}
			
			// If found is still true, this constructor is a match.
			if ( found == false)
				continue;
			
			_constructor = constructors[x];
			return;
		}
		
		// No constructor found.  Create a useful error message.
		throw new TypeStreamException( "TypeReaderAuto: No valid constructor: " + createError(objects) );
    }
    
    private String createError( Object[] objects )
    {
		StringBuffer error = new StringBuffer();
		error.append(_clss.getName());
		error.append("(");
		for ( int x=0; x < objects.length; x++ )
		{
			if ( objects[x] != null)
				error.append( objects[x].getClass().getName() );
			else
				error.append( "null");
			
			if ( x < objects.length-1 )
				error.append( "," );
		}
		error.append(")");

		return error.toString();
    }
}
