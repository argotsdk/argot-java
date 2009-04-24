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
package com.argot;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;

import com.argot.meta.MetaDefinition;

public final class TypeHelper
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
	
	/**
	 * This checks if the id, name & structure are the same as used
	 * in the map.  The byte[] must follow the TypeMapCore type id's.
	 * Any function or references must be valid in the context of this
	 * TypeMap.
	 * 
	 * This is like the register version below, however in some cases
	 * like a protocol you just need to check if the id's are the same.
	 */
	public static void isSame( int id, TypeLocation location, byte[] structure, ReferenceTypeMap coreMap )
	throws TypeException
	{
		TypeLibrary library = coreMap.getLibrary();
				
		// First check if we can find the same identifier.
		int i = library.getTypeId(location);
			
		// Are the identifiers the same.
		if ( id != i )
		{
			throw new TypeException("Type Mismatch: Type identifiers different");
		}

		// Are the definitions the same.
		// read the definition.
		
		TypeElement definition = readStructure( coreMap, structure );
		
		// check what we've read with the local version.
		TypeElement localStruct = library.getStructure( i );
		if (!TypeHelper.structureMatches( coreMap, definition, localStruct ))
		{
			throw new TypeException("Type mismatch: structures do not match: ");
		}
	}

	public static TypeElement readStructure( ReferenceTypeMap core, byte[] structure )
	throws TypeException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream( structure );
		TypeInputStream tmis = new TypeInputStream( bais, core);
		
		try
		{
			Object definition = tmis.readObject( MetaDefinition.TYPENAME );
			return (TypeElement) definition;
		}
		catch (IOException e)
		{
			throw new TypeException( "failed reading structure:" + e.getMessage() );
		}
		
	}

}
