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

import com.argot.meta.MetaReference;

import junit.framework.TestCase;

public class TestTypeMap
extends TestCase
{
    private TypeLibrary _library;
    private TypeMap _map;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        TypeMapCore.loadLibrary( _library );
        _map = TypeMapCore.getCoreTypeMap( _library );
    }
    
    public void testGetIdByName() throws Exception
    {
        int id = _map.getId( MetaReference.TYPENAME );
        assertEquals( id, TypeMapCore.REFERENCEID );
    }

    public void testGetSystemId() throws Exception
    {
        int id = _map.getId( MetaReference.TYPENAME );
        int systemId = _map.getSystemId( id );

        assertEquals( systemId, _library.getId( MetaReference.TYPENAME ));
    }
    
    public void testGetName() throws Exception
    {
        String name = _map.getName( TypeMapCore.REFERENCEID );
        assertEquals( MetaReference.TYPENAME, name );
    }
    
    public void testGetIdBySystemId() throws Exception
    {
        int id = _map.getId( _library.getId( MetaReference.TYPENAME ));
        assertEquals( TypeMapCore.REFERENCEID, id );
    }
}
