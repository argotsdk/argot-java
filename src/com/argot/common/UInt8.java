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

public class UInt8
implements TypeReader, TypeWriter
{
	public static String TYPENAME = "uint8";
	public static final String VERSION = "1.3";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		int i;

		i = in.getStream().read();
		
		if ( i == -1 ) 
			throw new IOException( "EOF" );
		
		return new Short( (short) i );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		if ( o instanceof Integer )
		{
			int b = ((Integer) o).intValue();
			if ( b < 0 || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );
				
			out.getStream().write( b );
		}
		else if ( o instanceof Short )
		{
			int b = ((Short) o).intValue();
			if ( b < 0 || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );

			out.getStream().write( b );
		}
		else if ( o instanceof Byte )
		{
			int b = ((Byte)o).intValue();
			if ( b < MIN || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );
				
			out.getStream().write( b );
		}
	}
	
	public static int MIN = 0;
	public static int MAX = 256-1;

}
