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

package com.argot.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.common.CommonLoader;
import com.argot.data.MixedData;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaExtensionLoader;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class MessageTest 
extends TestCase
{
	private TypeLibrary _library;
	private int _mixedDataTypeId;
	
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
        _mixedDataTypeId = MixedData.register( _library );
    }
    
    public void testTypeMapCore() throws Exception
    {
        MixedData data = new MixedData( 2345, (short)234, "Testing");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageWriter writer = new MessageWriter( _library );
        writer.writeMessage( baos, _mixedDataTypeId, data );
        baos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        Object o = MessageReader.readMessage( _library, bais );
        assertEquals( o.getClass(), data.getClass() );
        MixedData readData = (MixedData) data;
        assertEquals( readData.getInt(), data.getInt() );
        assertEquals( readData.getShort(), data.getShort() );
        assertEquals( readData.getString(), data.getString() );
        
     }
}
