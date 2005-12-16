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

public class BigEndianUnsignedInteger
implements TypeReader, TypeWriter
{
	
	public static final String TYPENAME = "u32";

	public Object read(TypeInputStream in, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d;
		
		a = in.getStream().read();
		b = in.getStream().read();
		c = in.getStream().read();
		d = in.getStream().read();
		
		long value = ((a << 24) + (b << 16) + (c << 8) + d);

		// need to return a long value here because an unsigned
		// integer can be bigger than the java int.
		return new Long( value );
	}

	public void write(TypeOutputStream out, Object o, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d;
		
		long s;
		
		if ( o instanceof Long )
		{
		    s = ((Long) o).longValue();
		}
		else if ( o instanceof Integer )
		{
		    s = ((Integer)o).longValue();
		}
		else
		{
			throw new TypeException( "U32B: requires Long" );
		}
		

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16B: value out of range:" +s + " " + MAX );
		
		a = (int)(s & (long) 0xff000000) >> 24;
		b = (int)(s & 0x00ff0000) >> 16;
		c = (int)(s & 0x0000ff00) >> 8;
		d = (int)(s & 0x000000ff);
		
		out.getStream().write( a );
		out.getStream().write( b );
		out.getStream().write( c );
		out.getStream().write( d );

	}
	
	public final long MIN = 0;
	
	// max value is 2^16 x 2^16..  just as easy way to write it.
	public final long MAX = (long)(65536l*65536l);//2^32-1;
	
	
	

}
