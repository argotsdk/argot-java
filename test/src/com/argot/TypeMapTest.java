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

import java.util.Iterator;

import com.argot.meta.MetaLoader;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;

import junit.framework.TestCase;

public class TypeMapTest
extends TestCase
{
    private TypeLibrary _library;
    private TypeMap _map;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        _library.loadLibrary( new MetaLoader() );
		_map = new TypeMap( _library, new TypeMapperCore(new TypeMapperLibrary()));
    }
    
    public void testGetIdByName() throws Exception
    {
        int id = _map.getStreamId( MetaReference.TYPENAME );
        assertEquals( TypeMapperCore.REFERENCE_ID, id );
    }

    public void testGetSystemId() throws Exception
    {
        int id = _map.getStreamId( MetaReference.TYPENAME );
        int systemId = _map.getDefinitionId( id );

        assertEquals( systemId, _library.getTypeId( MetaReference.TYPENAME, "1.3" ));
    }
    
    public void testGetName() throws Exception
    {
        MetaName name = _map.getName( TypeMapperCore.REFERENCE_ID );
        assertEquals( MetaReference.TYPENAME, name.toString() );
    }
    
    public void testGetIdBySystemId() throws Exception
    {
        int id = _map.getStreamId( _library.getTypeId( MetaReference.TYPENAME, "1.3" ));
        assertEquals( TypeMapperCore.REFERENCE_ID, id );
    }
    
    public void testIterator() throws Exception
    {
    	_map = new TypeMap( _library, new TypeMapperLibrary() );
    	_map.map( 34, 33 );
    	_map.map( 20, 34 );
    	_map.map( 45, 35 );
    	
    	Iterator iter = _map.getIdList().iterator();
    	
    	Integer i = (Integer) iter.next();
    	assertEquals( 20, i.intValue() );

    	i = (Integer) iter.next();
    	assertEquals( 34, i.intValue() );

    	i = (Integer) iter.next();
    	assertEquals( 45, i.intValue() );
    	
    	assertFalse( iter.hasNext() );
    }
}
