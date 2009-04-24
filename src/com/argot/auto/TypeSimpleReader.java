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

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;

public class TypeSimpleReader 
implements TypeLibraryReader,TypeBound
{
	private TypeReader _reader;
	
	public TypeSimpleReader(TypeReader reader)
	{
		_reader = reader;
	}
	
	protected TypeSimpleReader()
	{
		_reader = null;
	}

	protected void setReader(TypeReader reader)
	{
		_reader = reader;
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		if (_reader instanceof TypeBound && _reader != this)
		{
			((TypeBound)_reader).bind(library, definitionId, definition);
		}		
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return _reader;
	}



}
