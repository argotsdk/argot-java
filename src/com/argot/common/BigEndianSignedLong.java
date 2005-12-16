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
 */
public class BigEndianSignedLong
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "s64";
	
	public Object read(TypeInputStream in, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d,e,f,g,h;
		
		a = in.getStream().read(); //56
		b = in.getStream().read(); //48
		c = in.getStream().read(); //40
		d = in.getStream().read(); //32
		e = in.getStream().read(); //24
		f = in.getStream().read(); //16
		g = in.getStream().read(); //8
		h = in.getStream().read(); //0
		
		long value = (((long)a << 56) + ((long)b << 48) + ((long)c << 40) + ((long)d << 32) + ((long)e << 24) + ((long)f << 16) + ((long)g<<8) + (long)h);

		// need to return a long value here because an unsigned
		// integer can be bigger than the java int.
		return new Long( value );
	}

	public void write(TypeOutputStream out, Object o, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,c,d,e,f,g,h;
		
		if ( !(o instanceof Long) )
			throw new TypeException( "U32B: requires Long" );
		
		long s = ((Long) o).longValue();

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16B: value out of range");
		
		a = (int)((s >> 56) & 0xff);
		b = (int)((s >> 48) & 0xff);
		c = (int)((s >> 40) & 0xff);
		d = (int)((s >> 32) & 0xff);
		e = (int)((s >> 24) & 0xff);
		f = (int)((s >> 16) & 0xff);
		g = (int)((s >> 8) & 0xff);
		h = (int)(s & 0xff);
		
		out.getStream().write( a );
		out.getStream().write( b );
		out.getStream().write( c );
		out.getStream().write( d );
		out.getStream().write( e );
		out.getStream().write( f );
		out.getStream().write( g );
		out.getStream().write( h );

	}
	
	// NOTE: As java can not store anything bigger than
	//       a long.  It means that the max value is one
	//       bit short of the max of 2^64-1.
	public final long MIN = -9223372036854775808l; //-2^63;
	public final long MAX =  9223372036854775807l; //2^63-1;
	

}
