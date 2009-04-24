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
package com.argot.meta;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;

public abstract class MetaExpression
implements TypeElement
{
	public static final String TYPENAME = "meta.expression";
	public static final String VERSION = "1.3";
	
    private TypeLibrary _library;
    private TypeElement _definition;
    private int _memberTypeId;
    private int _typeId;
 
    public void bind(TypeLibrary library, int memberTypeId, TypeLocation location, TypeElement definition) 
    throws TypeException
    {
        _library = library;
        _definition = definition;
        _memberTypeId = memberTypeId;
        _typeId = _library.getTypeId( getTypeName() );
    }
    
    public TypeLibrary getLibrary()
    {
        return _library;
    }
    
    public TypeElement getTypeDefinition()
    {
        return _definition;
    }
    
    public int getMemberTypeId()
    {
        return _memberTypeId;
    }
    
    public int getTypeId()
    {
        return _typeId;
    }
    
    public abstract String getTypeName();
    

    
}
