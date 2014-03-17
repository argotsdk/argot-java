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
import com.argot.meta.MetaLoader;

public class CommonTest
extends TestCase
{

    protected TypeOutputStream out;
    private TypeMap map;
    private ByteArrayOutputStream baos;

	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new CommonLoader()
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
    
    
    public void testBigEndianSignedInteger() throws Exception
    {
		int i = -(int)(Math.PI*100000000l);
	
		Int32 besi = new Int32();
		besi.write( out, new Integer( i ) );

		TypeInputStream tmis = getInputStream();

		Integer si = (Integer) besi.read( tmis );
		assertEquals( i, si.intValue() );
    }
    
    public void testBigEndianSignedLong() throws Exception
    {
		long l = -(long)(Math.PI*1000000000000000000l);
		
		Int64 besl = new Int64();
		besl.write( out, new Long( l) );
		
		TypeInputStream tmis = getInputStream();

		Long sl = (Long) besl.read( tmis );
		assertEquals( l, sl.longValue() );
    }
    
    public void testBigEndianSignedShort() throws Exception
    {
		short s = -(short)(Math.PI*10000l);
			
		Int16 bess = new Int16();
		bess.write( out, new Integer( s ) );
		
		TypeInputStream tmis = getInputStream();
        
		Short sr = (Short) bess.read( tmis );
		assertEquals( s, sr.shortValue() );
    }
    
    public void testBigEndianUnsignedByte() throws Exception
    {
		short bs = (240);
        
		UInt8 beub = new UInt8();
		beub.write( out, new Short( bs ) );

		TypeInputStream tmis = getInputStream();
		
		Short us = (Short) beub.read( tmis );
		assertEquals( bs, us.shortValue() );
    }
    
    public void testBigEndianUnsignedShort() throws Exception
    {
		short s = (short)(Math.PI*10000l);
		
		UInt16 beus = new UInt16();
		beus.write( out, new Integer( s ) );

		TypeInputStream tmis = getInputStream();
		
		Integer us = (Integer) beus.read( tmis );		
		assertEquals( s, us.shortValue() );
    }
    
    public void testBigEndianUnsignedInteger() throws Exception
    {
		int i = (int)(Math.PI*100000000l);
        
		UInt32 beui = new UInt32();
		beui.write( out, new Long( i )); 
		TypeInputStream tmis = getInputStream();

		Long ui = (Long) beui.read( tmis );
		assertEquals( i, ui.longValue() );
    }
    
    public void testBigEndianUnsignedLong() throws Exception
    {
		long l = (long)(Math.PI*1000000000000000000l);

		UInt64 beul = new UInt64();
		beul.write( out, new Long( l ));
		
		TypeInputStream tmis = getInputStream();        

		Long ul = (Long) beul.read( tmis );
		assertEquals( l, ul.longValue() );
    }
    
}
