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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.argot.TypeConstructor;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeStreamException;

public class TypeConstructorAuto 
implements TypeConstructor
{
	private Class _clss;
	private Constructor _constructor;
	
	public TypeConstructorAuto( Class clss )
	{
		_clss = clss;
		_constructor = null;
	}

    public Object construct(TypeElement sequence, Object[] objects)
    throws TypeException
    {
    	if (_constructor == null)
    	{
    		resolveConstructor(objects);
    	}
    	
		try
		{
			return _constructor.newInstance( objects );
		}
		catch (IllegalArgumentException e)
		{
			e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + e.toString() + ":" + createError(objects) );						
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + e.toString() );
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
			throw new TypeException( "TypeReaderAuto:" + e.toString() );
		}
		catch (InvocationTargetException e)
		{
			e.printStackTrace();
				
			throw new TypeException( "TypeReaderAuto:" + e.toString() + ":" + e.getCause().getMessage() );
		}
		
		
    }

    private void resolveConstructor(Object[] objects)
    throws TypeException
    {    
		Constructor constructors[] = _clss.getConstructors();

		// Loop through all the constructors on the class.
		for ( int x=0; x < constructors.length; x++ )
		{
			// First check if the parameter lengths match.
			if (  constructors[x].getParameterTypes().length != objects.length )
				continue;
			
			// Assume the parameters are the same until we find a parameter that doesn't match.
			boolean found = true;
			Class[] paramTypes = constructors[x].getParameterTypes();
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
