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

public class UInt32
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "uint32";
	public static final String VERSION = "1.3";

	public Object read(TypeInputStream in ) 
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

	public void write(TypeOutputStream out, Object o ) 
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
