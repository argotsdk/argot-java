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
