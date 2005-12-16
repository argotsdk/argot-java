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
 * This is a basic 16-bit unsigned int value stored in big endian format.
 * Reads and writes Integer type.
 */
public class BigEndianUnsignedShort
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "u16";
	
	public Object read(TypeInputStream in, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b;
		
		a = in.getStream().read();
		if ( a == -1 ) throw new IOException( "end of stream" );
		b = in.getStream().read();
		if ( b == -1 ) throw new IOException( "end of stream" );
		
		return new Integer( ((a << 8) + b) );

	}

	public void write(TypeOutputStream out, Object o, TypeElement element ) 
	throws TypeException, IOException
	{
		int a,b,s;
		if ( o instanceof Integer )
		{
			s = ((Integer) o).intValue();
		}
		else if ( o instanceof Short )
		{
			s = ((Short) o).intValue();
		}
		else
			throw new TypeException( "BigEndianShort requires Short or Integer object");

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16: value out of range:"+ s);

		a = (s & 0xff00) >> 8;
		b = (s & 0x00ff);

		out.getStream().write( a );
		out.getStream().write( b );

	}
	
	public static final int MIN = 0;
	public static final int MAX = 65536; //2^16-1; 

}
