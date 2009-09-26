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
package com.argot.network;

import junit.framework.TestCase;

import com.argot.ReferenceTypeMap;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.CommonLoader;
import com.argot.common.Int32;
import com.argot.common.UInt32;
import com.argot.common.UInt8;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaExtensionLoader;
import com.argot.meta.MetaLoader;

public class TypeNetworkTest
extends TestCase
{
	private TypeLibrary _library;
	
	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new MetaExtensionLoader(),
		new CommonLoader()
	};
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary( libraryLoaders );
    }

    public void testClientMetaDictionaryUsage() throws Exception
    {
    	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
		//TypeMapCore.mapMeta( serverMap, _library );
		//serverMap.map( 42, _library.getDefinitionId("dictionary.words","1.3"));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	// client will write to server and expect a response.
    	// server will need to operate on different thread.
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	int cid = clientMap.getStreamId( UInt32.TYPENAME );
    	int sid = serverMap.getStreamId( UInt32.TYPENAME );
    	assertEquals( sid, cid );
    	
    	// If we used the meta dictionary.  We should only
    	// need a single request/response pair.
    	assertEquals( 2, transport.getConnectionCount() );
    }
    
    public void testClientResolutionGetIdName() throws Exception
    {
       	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	// client will write to server and expect a response.
    	// server will need to operate on different thread.
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	int cid = clientMap.getStreamId( UInt32.TYPENAME );
    	int sid = serverMap.getStreamId( UInt32.TYPENAME );
    	assertEquals( sid, cid ); 	
    }

    public void testClientResolutionReverse() throws Exception
    {
       	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
		
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	serverMap.map( 84, _library.getDefinitionId( Int32.TYPENAME, Int32.VERSION ));
    	int cid = clientMap.getDefinitionId( 84 );
    	int sid = _library.getDefinitionId( Int32.TYPENAME, Int32.VERSION );
    	assertEquals( sid, cid );
    }

    public void testClientResolutionLibraryId() throws Exception
    {
       	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
   	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	int cid = clientMap.getStreamId( _library.getDefinitionId( UInt32.TYPENAME, UInt32.VERSION));
    	int sid = serverMap.getStreamId( _library.getDefinitionId(UInt32.TYPENAME, UInt32.VERSION));
    	assertEquals( sid, cid );    	
    }

    public void testClientResolutionReader() throws Exception
    {
       	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
   	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	TypeReader cReader = clientMap.getReader( clientMap.getStreamId( UInt8.TYPENAME ) );
    	TypeReader sReader = serverMap.getReader( serverMap.getStreamId( UInt8.TYPENAME ) );
    	assertEquals( cReader, sReader );    	
    }

    public void testClientResolutionWriter() throws Exception
    {
       	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperCore(new TypeMapperError() )));
   	
    	TypeServer typeServer = new TypeServer( _library, serverMap );
    	TestTypeTransport transport = new TestTypeTransport( typeServer );
    	TypeClient typeClient = new TypeClient( _library, transport );
    	ReferenceTypeMap clientMap = new ReferenceTypeMap( _library, new TypeMapperCore( new DynamicClientTypeMapper( typeClient ) ) );
    	
    	TypeWriter cWriter = clientMap.getWriter( clientMap.getStreamId( UInt8.TYPENAME ) );
    	TypeWriter sWriter = serverMap.getWriter( serverMap.getStreamId( UInt8.TYPENAME ) );
    	assertEquals( cWriter, sWriter );    	    	
    }
    
    public void testProcessServiceMessage() throws Exception
    {
    	ReferenceTypeMap serverMap = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperError() ) );
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
