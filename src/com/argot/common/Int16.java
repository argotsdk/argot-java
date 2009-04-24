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

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;


/**
 * This is a basic 16-bit unsigned int value stored in big endian format.
 * Reads and writes Integer type.
 */
public class Int16
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "int16";
	public static final String VERSION = "1.3";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		byte[] buffer = new byte[2];
		
		in.read(buffer,0,2);
		return new Short( (short) (((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff)) );

	}

	public void write(TypeOutputStream out, Object o ) 
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
			throw new TypeException( "U16B: value out of range" + s);

		// This is going to turn our signed value into a unsigned
		// 16 bits.
		s = s & 0xffff;
		
		a = (s & 0xff00) >> 8;
		b = (s & 0x00ff);

		out.getStream().write( a );
		out.getStream().write( b );

	}
	
	public static final int MIN = -32768; // -2^15;
	public static final int MAX =  32767; // 2^15-1; 
}
