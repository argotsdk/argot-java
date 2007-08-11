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
package com.argot.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeMapCore;
import com.argot.TypeLibrary;
import com.argot.dictionary.Dictionary;
import com.argot.meta.MetaLoader;

public class MetaDictionaryTest
extends TestCase
{
	private TypeLibrary _library;

	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader()
	};
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary( libraryLoaders );
    }
    
    public void testTypeMapCore() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        
        TypeMap map = TypeMapCore.getCoreTypeMap( _library );
		Dictionary.writeDictionary( baos, map );
		
		byte[] coreMapBytes = baos.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream( coreMapBytes );
		TypeMap mapRead = Dictionary.readDictionary( _library, bais );
		
		Iterator iter1 = mapRead.getIterator();

		while( iter1.hasNext() )
		{			
		    Integer id = (Integer) iter1.next();
		    
		    int o1 = mapRead.getSystemId( id.intValue() );
		    int o2 = map.getSystemId( id.intValue() );
		    
		    assertEquals( o1, o2 );
		    
		    System.out.println( "o1: " + o1 + " == o2:" + o2 );
		}
    }
}
