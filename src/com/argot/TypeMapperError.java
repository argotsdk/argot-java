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

import com.argot.meta.MetaName;

public class TypeMapperError 
implements TypeMapper 
{
	private TypeLibrary _library;

	public TypeMapperError()
	{
	}
	
	public void initialise(TypeMap map) 
	throws TypeException 
	{
		_library = map.getLibrary();
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		throw new TypeException("not mapped: " + _library.getName(definitionId));
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		throw new TypeException("not mapped");
	}

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		MetaName name = _library.getName(nameId);
		throw new TypeException("not mapped: " + name );
	}

}
