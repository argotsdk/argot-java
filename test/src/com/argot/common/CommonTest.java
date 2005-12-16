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
package com.argot.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.argot.TypeLibrarySingleton;
import com.argot.TypeMap;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;

public class CommonTest
extends TestCase
{

    protected TypeOutputStream out;
    private TypeMap map;
    private ByteArrayOutputStream baos;

    protected void setUp() throws Exception
    {
        super.setUp();
        
		baos = new ByteArrayOutputStream();
        TypeLibrary library = TypeLibrarySingleton.getDefault();
		map = new TypeMap( library );
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
	
		BigEndianSignedInteger besi = new BigEndianSignedInteger();
		besi.write( out, new Integer( i ), null );

		TypeInputStream tmis = getInputStream();

		Integer si = (Integer) besi.read( tmis, null );
		assertEquals( i, si.intValue() );
    }
    
    public void testBigEndianSignedLong() throws Exception
    {
		long l = -(long)(Math.PI*1000000000000000000l);
		
		BigEndianSignedLong besl = new BigEndianSignedLong();
		besl.write( out, new Long( l), null );
		
		TypeInputStream tmis = getInputStream();

		Long sl = (Long) besl.read( tmis, null );
		assertEquals( l, sl.longValue() );
    }
    
    public void testBigEndianSignedShort() throws Exception
    {
		short s = -(short)(Math.PI*10000l);
			
		BigEndianSignedShort bess = new BigEndianSignedShort();
		bess.write( out, new Integer( s ), null );
		
		TypeInputStream tmis = getInputStream();
        
		Short sr = (Short) bess.read( tmis, null );
		assertEquals( s, sr.shortValue() );
    }
    
    public void testBigEndianUnsignedByte() throws Exception
    {
		short bs = (240);
        
		BigEndianUnsignedByte beub = new BigEndianUnsignedByte();
		beub.write( out, new Short( bs ), null );

		TypeInputStream tmis = getInputStream();
		
		Short us = (Short) beub.read( tmis, null );
		assertEquals( bs, us.shortValue() );
    }
    
    public void testBigEndianUnsignedShort() throws Exception
    {
		short s = (short)(Math.PI*10000l);
		
		BigEndianUnsignedShort beus = new BigEndianUnsignedShort();
		beus.write( out, new Integer( s ), null );

		TypeInputStream tmis = getInputStream();
		
		Integer us = (Integer) beus.read( tmis, null );		
		assertEquals( s, us.shortValue() );
    }
    
    public void testBigEndianUnsignedInteger() throws Exception
    {
		int i = (int)(Math.PI*100000000l);
        
		BigEndianUnsignedInteger beui = new BigEndianUnsignedInteger();
		beui.write( out, new Long( i ), null); 
		TypeInputStream tmis = getInputStream();

		Long ui = (Long) beui.read( tmis, null );
		assertEquals( i, ui.longValue() );
    }
    
    public void testBigEndianUnsignedLong() throws Exception
    {
		long l = (long)(Math.PI*1000000000000000000l);

		BigEndianUnsignedLong beul = new BigEndianUnsignedLong();
		beul.write( out, new Long( l ), null);
		
		TypeInputStream tmis = getInputStream();        

		Long ul = (Long) beul.read( tmis, null );
		assertEquals( l, ul.longValue() );
    }
    
}
