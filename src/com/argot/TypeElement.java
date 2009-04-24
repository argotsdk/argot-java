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

/**
 * A type element is any element which is used in the definition of a 
 * type itself.
 */
public interface TypeElement
{
    public void bind( TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition ) 
    throws TypeException;
    
	public TypeLibrary getLibrary();
	public TypeElement getTypeDefinition();
	public String getTypeName();
	public int getTypeId();    
}
