/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.argot.dictionary.Dictionary;

public abstract class ResourceDictionaryLoader
implements TypeLibraryLoader
{
	private String _resource; 

	public ResourceDictionaryLoader( String resource )
	{
		_resource = resource;
	}
	
	private InputStream getDictionaryStream( String location )
	{
		File dictionaryFile = new File( location );
		if ( dictionaryFile.exists())
		{
			try
			{
				return new FileInputStream( dictionaryFile );
			}
			catch (FileNotFoundException e)
			{
				// ignore and drop through.
			}
		}

		ClassLoader cl = this.getClass().getClassLoader();
		InputStream is = cl.getResourceAsStream( location );
		if ( is == null )
		{
			return null;
		}				
		return is;
	}
	
	
	public void load( TypeLibrary library ) 
	throws TypeException
	{
		InputStream is = getDictionaryStream( _resource );
		if ( is == null )
		{
			throw new TypeException("Failed to load:" + _resource );
		}
		
		try
		{
			Dictionary.readDictionary( library, is );
		}
		catch (TypeException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		catch (IOException e)
		{
			throw new TypeException("Error loading dictionary: " + _resource, e );
		}
		
		bind(library);
	}
	
	public abstract void bind( TypeLibrary library ) 
	throws TypeException;

}
