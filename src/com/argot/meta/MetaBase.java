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
package com.argot.meta;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;

public abstract class MetaBase
implements TypeElement
{
    private TypeLibrary _library;
    private TypeElement _definition;
    private String _memberTypeName;
    private int _memberTypeId;
    private int _typeId;
 
    public void bind(TypeLibrary library, TypeElement definition, String memberTypeName, int memberTypeId) throws TypeException
    {
        _library = library;
        _definition = definition;
        _memberTypeName = memberTypeName;
        _memberTypeId = memberTypeId;
        _typeId = _library.getId( getTypeName() );
    }
    
    public TypeLibrary getLibrary()
    {
        return _library;
    }
    
    public TypeElement getTypeDefinition()
    {
        return _definition;
    }

    public String getMemberTypeName()
    {
        return _memberTypeName;
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
