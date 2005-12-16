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

public class ReferenceTypeMap
extends TypeMap
{
	private TypeMap _refMap;
	
    public ReferenceTypeMap(TypeLibrary library, TypeMap map)
    {
        super(library);
        _refMap = map;
    }

    public TypeMap referenceMap() throws TypeException
	{
        if ( _refMap == null )
            throw new TypeException("Reference Type Map not set");
        
		return _refMap;
	}
	
	public void setReferenceMap( TypeMap map )
	{
		_refMap = map;
	}
}
