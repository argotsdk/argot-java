/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
package com.argot;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class TypeConstructorAuto 
implements TypeConstructor
{
	private Class _clss;
	
	public TypeConstructorAuto( Class clss )
	{
		_clss = clss;
	}

    public Object construct(Object[] objects) throws TypeException
    {       
		Constructor constructors[] = _clss.getConstructors();

		for ( int x=0; x < constructors.length; x++ )
		{
			if (  constructors[x].getParameterTypes().length == objects.length )
			{
				boolean found = true;
				Class[] paramTypes = constructors[x].getParameterTypes();
				for ( int y=0; y < paramTypes.length; y++ )
				{
					if ( objects[y] != null )
						
					if ( !paramTypes[y].isInstance( objects[y] ) )
					{
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
													
						found = false;
						break;
					}
				}
					
				if ( found == false)
					continue;
					
				try
				{
					return constructors[x].newInstance( objects );
				}
				catch (IllegalArgumentException e)
				{
					e.printStackTrace();
					throw new TypeException( "TypeReaderAuto:" + e.toString() );						
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
			

		}
		
		throw new TypeException( "TypeReaderAuto: No valid constructors found." + _clss.getName() );
    }

}
