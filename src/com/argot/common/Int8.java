/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
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
			throw new TypeException( "int8: object not supported");

		// Check the range.
		if ( b < MIN || b > MAX )
			throw new TypeException( "int8: out of range: " + b );

		// We take the int value and & 0xff to take the lower
		// eight bits.  This will convert the value to between
		// 0 and 255 discarding upper values.
			
		b = b & 0xff;
				
		out.getStream().write( b );

	}

	public static int MIN = -128; //-(2^7);
	public static int MAX = 127;  //2^7-1;
}
