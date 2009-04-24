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
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeWriter;

public class TypeSimpleWriter 
implements TypeLibraryWriter,TypeBound
{
	private TypeWriter _writer;
	
	public TypeSimpleWriter(TypeWriter writer)
	{
		_writer = writer;
	}
	
	protected TypeSimpleWriter()
	{
		_writer = null;
	}
	
	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		if (_writer instanceof TypeBound && _writer != this)
		{
			((TypeBound)_writer).bind(library, definitionId, definition);
		}		
	}
	
	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return _writer;
	}
	
	protected void setWriter(TypeWriter writer)
	{
		_writer = writer;
	}

}
