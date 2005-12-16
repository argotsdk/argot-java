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

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a basic 32-bit signed int value stored in big endian format,
 * two's compliment.  
 */
public class BigEndianSignedInteger
implements TypeReader, TypeWriter
{
	
	public static final String TYPENAME = "s32";

	public Object read(TypeInputStream in, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d;
		
		a = in.getStream().read();
		b = in.getStream().read();
		c = in.getStream().read();
		d = in.getStream().read();
		
		int value = ((a << 24) + (b << 16) + (c << 8) + d);

		// need to return a long value here because an unsigned
		// integer can be bigger than the java int.
		return new Integer( value );
	}

	public void write(TypeOutputStream out, Object o, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d;
		
		if ( !(o instanceof Integer) )
			throw new TypeException( "U32B: requires Integer" );
		
		int s = ((Integer) o).intValue();

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16B: value out of range" + s );
		
		// This is going to turn our signed value into a unsigned
		// 16 bits.
		s = s & 0xffffffff;
		
		a = (int)((s & 0xff000000) >> 24);
		b = (int)((s & 0x00ff0000) >> 16);
		c = (int)((s & 0x0000ff00) >> 8);
		d = (int)(s & 0x000000ff);
		
		out.getStream().write( a );
		out.getStream().write( b );
		out.getStream().write( c );
		out.getStream().write( d );

	}
	
	public final long MIN = -2147483648; //-2^31;
	public final long MAX =  2147483647; //2^31-1;
	
}
