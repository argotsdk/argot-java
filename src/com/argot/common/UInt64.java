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

public class UInt64
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "uint64";
	public static final String VERSION = "1.3";
	
	public Object read(TypeInputStream in ) 
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

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		int a,b,c,d,e,f,g,h;
		
		if ( !(o instanceof Long) )
			throw new TypeException( "U32B: requires Long" );
		
		long s = ((Long) o).longValue();

		if ( s < MIN || s > MAX )
			throw new TypeException( "U16B: value out of range: " + s );
		
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
	//       bit short of the max of 2^64.
	public final long MIN = 0;
	public final long MAX = 9223372036854775807l; //2^63-1;
	

}
