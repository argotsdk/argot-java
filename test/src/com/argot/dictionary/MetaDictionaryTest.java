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
package com.argot.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import com.argot.TypeMap;
import com.argot.TypeMapCore;
import com.argot.TypeLibrary;
import com.argot.dictionary.Dictionary;

public class MetaDictionaryTest
extends TestCase
{
	private TypeLibrary _library;
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        TypeMapCore.loadLibrary( _library );
        DictionaryMap.loadDictionaryMap( _library );
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
		Iterator iter2 = map.getIterator();
		while( iter1.hasNext() || iter2.hasNext() )
		{
		    Object o1 = iter1.next();
		    Object o2 = iter2.next();
		    assertEquals( o1, o2 );
		    
		    System.out.println( "o1: " + o1.toString() + " == o2:" + o2.toString() );
		}
    }
}
