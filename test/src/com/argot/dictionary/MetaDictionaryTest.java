/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import junit.framework.TestCase;

import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;

import com.argot.TypeLibrary;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperError;
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
        
		TypeMap map = new TypeMap( _library, new TypeMapperCore(new TypeMapperError()));

		Dictionary.writeDictionary( baos, map );
		
		byte[] coreMapBytes = baos.toByteArray();
		
		ByteArrayInputStream bais = new ByteArrayInputStream( coreMapBytes );
		TypeMap mapRead = Dictionary.readDictionary( _library, bais );
		
		Iterator<Integer> iter1 = mapRead.getIdList().iterator();

		while( iter1.hasNext() )
		{			
		    Integer id = (Integer) iter1.next();
		    
		    int o1 = mapRead.getDefinitionId( id.intValue() );
		    int o2 = map.getDefinitionId( id.intValue() );
		    
		    assertEquals( o1, o2 );
		    
		    System.out.println( "o1: " + o1 + " == o2:" + o2 );
		}
    }
}
