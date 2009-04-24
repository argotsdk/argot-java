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


public class TypeMapperDirect 
implements TypeMapper
{
	TypeLibrary _library;
	TypeMap _map;
	
	public void initialise(TypeMap map) 
	throws TypeException 
	{
		_library = map.getLibrary();
		_map = map;
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		_map.map(definitionId, definitionId);
		
		return definitionId;
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		throw new TypeException("Call should not be reached");
	}

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		int defId = _map.getDefinitionId(nameId);
		TypeElement elemStructure = _library.getStructure(defId);
		return -1;
	}

}
