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
 * This is a basic 16-bit unsigned int value stored in big endian format.
 * Reads and writes Integer type.
 */
public class UInt16
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "uint16";
	
	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		byte[] buffer = new byte[2];
		
		in.read(buffer,0,2);
		return new Integer( ((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff) );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		byte[] buffer = new byte[2];
		int s;
		
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
			throw new TypeException( "u16: value out of range:"+ s);
		
		buffer[0] = (byte)((s >> 8) & 0xff );
		buffer[1] = (byte)(s & 0xff );
		out.getStream().write(buffer,0,2);
	}
	
	public static final int MIN = 0;
	public static final int MAX = 65536; //2^16-1; 

}
