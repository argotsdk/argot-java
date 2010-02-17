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
package com.argot.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeInputStream;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaLoader;

public class UVInt28Test
extends TestCase
{

    protected TypeOutputStream out;
    private TypeMap map;
    private ByteArrayOutputStream baos;

	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader()
	};

    protected void setUp() throws Exception
    {
        super.setUp();
        
		baos = new ByteArrayOutputStream();
        TypeLibrary library = new TypeLibrary( libraryLoaders );
		map = new TypeMap( library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())) );
        out = new TypeOutputStream( baos, map );        
    }

    protected TypeInputStream getInputStream()
    {
		System.out.println( "reading...." );
		ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
		return new TypeInputStream( bais, map );
    }
    
    protected void tearDown() throws Exception
    {
        super.tearDown();
    }
    
    
    public void testUnsignedVariableInteger28singleByte() throws Exception
    {
		int i = 10;
	
		UVInt28 besi = new UVInt28();
		besi.write( out, new Integer( i ) );

		TypeInputStream tmis = getInputStream();

		assertEquals(1,baos.size());
		
		Integer si = (Integer) besi.read( tmis );
		assertEquals( i, si.intValue() );
    }
    
    public void testUnsignedVariableInteger28doubleByte() throws Exception
    {
		int i = 150;
	
		System.out.println("Writing: " + Integer.toBinaryString(i) );
		UVInt28 besi = new UVInt28();
		besi.write( out, new Integer( i ) );

		TypeInputStream tmis = getInputStream();

		assertEquals(2, baos.size());
		
		Integer si = (Integer) besi.read( tmis );
		assertEquals( i, si.intValue() );
    }
    
    public void testUnsignedVariableInteger28tripleByte() throws Exception
    {
		int i = 18000;

		System.out.println("Writing: " + Integer.toBinaryString(i) );

		UVInt28 besi = new UVInt28();
		besi.write( out, new Integer( i ) );

		TypeInputStream tmis = getInputStream();

		assertEquals(3, baos.size());
		
		Integer si = (Integer) besi.read( tmis );
		assertEquals( i, si.intValue() );
    }
  
    public void testUnsignedVariableInteger28quadByte() throws Exception
    {
		int i = 2500000;

		System.out.println("Writing: " + Integer.toBinaryString(i) );

		UVInt28 besi = new UVInt28();
		besi.write( out, new Integer( i ) );

		TypeInputStream tmis = getInputStream();

		assertEquals( 4, baos.size());
		
		Integer si = (Integer) besi.read( tmis );
		assertEquals( i, si.intValue() );
    }
    
    public void testUnsignedVariableInteger28negativeError() throws Exception
    {
		int i = -10;
	
		UVInt28 besi = new UVInt28();
		
		try
		{
			besi.write( out, new Integer( i ) );
			fail("should throw error.");
		}
		catch(Exception ex)
		{
			
		}
    }
    
    public void testUnsignedVariableInteger28overflowError() throws Exception
    {
		int i = (1<<28)+1;

		System.out.println("Writing: " + Integer.toBinaryString(i) );

		UVInt28 besi = new UVInt28();
		
		try
		{
			besi.write( out, new Integer( i ) );
			fail("should throw error.");
		}
		catch(Exception ex)
		{
			
		}
    }    
}
