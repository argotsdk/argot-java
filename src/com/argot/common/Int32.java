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

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a basic 32-bit signed int value stored in big endian format,
 * two's compliment.  
 */
public class Int32
implements TypeReader, TypeWriter
{
	
	public static final String TYPENAME = "int32";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		byte bytes[] = new byte[4];
		in.read(bytes,0,4);
		
		int value = (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff ));

		// need to return a long value here because an unsigned
		// integer can be bigger than the java int.
		return new Integer( value );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		if ( !(o instanceof Integer) )
			throw new TypeException( "s32: requires Integer" );
		
		int s = ((Integer) o).intValue();

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16B: value out of range" + s );
		
		// This is going to turn our signed value into a unsigned
		// 16 bits.
		byte[] bytes = new byte[4];
		
		bytes[0] = (byte)((s >> 24) & 0xff );
		bytes[1] = (byte)((s >> 16) & 0xff );
		bytes[2] = (byte)((s >> 8) & 0xff );
		bytes[3] = (byte)(s & 0xff );
		
		out.getStream().write(bytes,0,4);
	}
	
	public final long MIN = -2147483648; //-2^31;
	public final long MAX =  2147483647; //2^31-1;
}
