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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

public class TypeHelper
{
	public static byte[] toByteArray( ReferenceTypeMap core , TypeElement definition )
	throws TypeException
	{
		return resolveStructure( core, definition );
	}
	
	public static boolean structureMatches( ReferenceTypeMap core, TypeElement definition1, byte[] definition2 )
	{
		try
		{
			byte[] struct1 = resolveStructure( core, definition1 );		
			return Arrays.equals( struct1, definition2 );
		}
		catch (TypeException e)
		{
			e.printStackTrace();
			return false;
		}	    
	}
	
	public static boolean structureMatches( ReferenceTypeMap core, TypeElement definition1, TypeElement definition2 )
	{
		try
		{
			byte[] struct1 = resolveStructure( core, definition1 );
			byte[] struct2 = resolveStructure( core, definition2 );	
			
			return Arrays.equals( struct1, struct2 );
		}
		catch (TypeException e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	private static byte[] resolveStructure( ReferenceTypeMap refMap, TypeElement definition )
	throws TypeException
	{
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TypeOutputStream tmos = new TypeOutputStream( out, refMap );
		try
		{
			tmos.writeObject( "meta.definition", definition );
		}
		catch (IOException e)
		{
			e.printStackTrace();
			throw new TypeException( "failed to write the definition", e );
		}
		byte b[] = out.toByteArray();
		
		return b;			
	}
	
}
