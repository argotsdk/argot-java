/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

import junit.framework.TestCase;

import com.argot.DynamicTypeMap;
import com.argot.TypeLibraryLoader;
import com.argot.TypeLibrary;
import com.argot.TypeMapCore;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.CommonLoader;
import com.argot.common.Int32;
import com.argot.common.UInt32;
import com.argot.common.UInt8;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaLoader;

public class TypeNetworkTest
extends TestCase
{
	private TypeLibrary _library;
	
	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new CommonLoader()
	};
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary( libraryLoaders );
    }

    public void testClientMetaDictionaryUsage() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	// client will write to server and expect a response.
    	// server will need to operate on different thread.
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	int cid = clientMap.getId( UInt8.TYPENAME );
    	int sid = serverMap.getId( UInt8.TYPENAME );
    	assertEquals( sid, cid );
    	
    	// If we used the meta dictionary.  We should only
    	// need a single request/response pair.
    	assertEquals( 1, transport.getConnectionCount() );
    }
    
    public void testClientResolutionGetIdName() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	// client will write to server and expect a response.
    	// server will need to operate on different thread.
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	int cid = clientMap.getId( UInt8.TYPENAME );
    	int sid = serverMap.getId( UInt8.TYPENAME );
    	assertEquals( sid, cid ); 	
    }

    public void testClientResolutionReverse() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	serverMap.map( 54, _library.getId( Int32.TYPENAME ));
    	int cid = clientMap.getSystemId( 54 );
    	int sid = _library.getId( Int32.TYPENAME );
    	assertEquals( sid, cid );
    }

    public void testClientResolutionLibraryId() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
    	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	int cid = clientMap.getId( _library.getId( UInt32.TYPENAME));
    	int sid = serverMap.getId( UInt32.TYPENAME );
    	assertEquals( sid, cid );    	
    }

    public void testClientResolutionReader() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
    	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	TypeReader cReader = clientMap.getReader( clientMap.getId( UInt8.TYPENAME ) );
    	TypeReader sReader = serverMap.getReader( serverMap.getId( UInt8.TYPENAME ) );
    	assertEquals( cReader, sReader );    	
    }

    public void testClientResolutionWriter() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
		TypeMapCore.mapMeta( serverMap, _library );
		serverMap.map( 42, _library.getId("dictionary.words"));
    	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	DynamicClientTypeMap clientMap = new DynamicClientTypeMap( _library, typeClient );
    	
    	TypeWriter cWriter = clientMap.getWriter( clientMap.getId( UInt8.TYPENAME ) );
    	TypeWriter sWriter = serverMap.getWriter( serverMap.getId( UInt8.TYPENAME ) );
    	assertEquals( cWriter, sWriter );    	    	
    }
    
    public void testProcessServiceMessage() throws Exception
    {
    	DynamicTypeMap serverMap = new DynamicTypeMap( _library );
    	TestService service = new TestService();
    	TypeServer typeServer = new TypeServer( _library, serverMap, service );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );

    	String msgSent = "Hello";
    	byte receive[] = new byte[100];
    	TypeEndPoint connection = typeClient.openLink();
    	connection.getOutputStream().write( msgSent.getBytes() );
    	int read = connection.getInputStream().read( receive );
    	
    	String msgReceived = new String( receive, 0, read, "UTF8" );
    	assertEquals( msgSent, msgReceived );    	    	
    }    
}
