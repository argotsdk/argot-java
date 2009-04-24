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
 * This represents a 8bit signed byte in big endian format.  Its a basic
 * type and returns an object of type Integer.  A java byte represents
 * a value of -127 to 127.
 */
public class Int8
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "int8";
	public static final String VERSION = "1.3";
	
	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		int i;

		i = in.read();
				
		if ( i > 127 ) i = (i & 0x7F) + 128;

		// This should take the lower eight bits of the integer
		// and convert them to a byte of value -127 to +127.  The
		// value is then converted back to int.
		return new Byte( (byte)i );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		int b;
		
		if ( o instanceof Integer )
		{
			// convert value to signed int.
			b = ((Integer) o).intValue();			
		}
		else if ( o instanceof Short )
		{
			b = ((Short) o).intValue();
		}
		else if ( o instanceof Byte )
		{
			b = ((Byte)o).intValue();
		}
		else
			throw new TypeException( "S8B: object not supported");

		// Check the range.
		if ( b < MIN || b > MAX )
			throw new TypeException( "U8B: out of range: " + b );

		// We take the int value and & 0xff to take the lower
		// eight bits.  This will convert the value to between
		// 0 and 255 discarding upper values.
			
		b = b & 0xff;
				
		out.getStream().write( b );

	}

	public static int MIN = -128; //-(2^7);
	public static int MAX = 127;  //2^7-1;
}
