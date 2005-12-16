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
package com.argot.network;

import java.io.FileInputStream;

import junit.framework.TestCase;

import com.argot.DynamicTypeMap;
import com.argot.TypeBindCommon;
import com.argot.TypeMapCore;
import com.argot.TypeLibrary;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.dictionary.Dictionary;
import com.argot.dictionary.DictionaryMap;

public class TypeNetworkTest
extends TestCase
{
	private TypeLibrary _library;
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        TypeMapCore.loadLibrary( _library );
        DictionaryMap.loadDictionaryMap( _library );
        Dictionary.readDictionary( _library, new FileInputStream("test/data/common.dictionary"));
        TypeBindCommon.bindCommon( _library );        
    }
    
    public void testClientResolutionGetIdName() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TypeClient typeClient = new TypeClient( _library, typeServer );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	int cid = clientMap.getId( "u8" );
    	int sid = serverMap.getId( "u8" );
    	assertEquals( sid, cid ); 	
    }

    public void testClientResolutionReverse() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TypeClient typeClient = new TypeClient( _library, typeServer );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	serverMap.map( 54, _library.getId( "s32" ));
    	int cid = clientMap.getSystemId( 54 );
    	int sid = _library.getId( "s32" );
    	assertEquals( sid, cid );
    }

    public void testClientResolutionLibraryId() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TypeClient typeClient = new TypeClient( _library, typeServer );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	int cid = clientMap.getId( _library.getId( "u32"));
    	int sid = serverMap.getId( "u32" );
    	assertEquals( sid, cid );    	
    }

    public void testClientResolutionReader() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TypeClient typeClient = new TypeClient( _library, typeServer );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	TypeReader cReader = clientMap.getReader( clientMap.getId( "u8" ) );
    	TypeReader sReader = serverMap.getReader( serverMap.getId( "u8" ) );
    	assertEquals( cReader, sReader );    	
    }

    public void testClientResolutionWriter() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TypeClient typeClient = new TypeClient( _library, typeServer );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	TypeWriter cWriter = clientMap.getWriter( clientMap.getId( "u8" ) );
    	TypeWriter sWriter = serverMap.getWriter( serverMap.getId( "u8" ) );
    	assertEquals( cWriter, sWriter );    	    	
    }
    
    
}
