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

import com.argot.TypeInputStream;

public class BigEndianUnsignedByteTest
extends CommonTest
{
    public void testWriteBigEndianSignedByte() throws Exception
    {
		byte b = -114;
        
		BigEndianSignedByte besb = new BigEndianSignedByte();
		besb.write( out, new Byte(b), null);

		TypeInputStream tmis = getInputStream();
		
		Integer sb = (Integer) besb.read( tmis, null );
		assertEquals( b, sb.intValue() );
    }

}
