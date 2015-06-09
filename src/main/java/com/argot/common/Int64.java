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

public class Int64
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "int64";
	public static final String VERSION = "1.3";
	
	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		byte bytes[] = new byte[8];
		in.getStream().read(bytes,0,8);

		long value = ((((long)bytes[0] & 0xff) << 56) 
				    | (((long)bytes[1] & 0xff) << 48) 
				    | (((long)bytes[2] & 0xff) << 40) 
				    | (((long)bytes[3] & 0xff) << 32) 
				    | (((long)bytes[4] & 0xff) << 24) 
				    | (((long)bytes[5] & 0xff) << 16) 
				    | (((long)bytes[6] & 0xff) << 8) 
				    | ((long)bytes[7] & 0xff ));		

		return new Long( value );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		if ( !(o instanceof Long) )
			throw new TypeException( "Int64: requires Long" );
		
		long s = ((Long) o).longValue();
		byte[] bytes = new byte[8];
		
		bytes[0] = (byte) ((s >> 56) & 0xff );
		bytes[1] = (byte) ((s >> 48) & 0xff );
		bytes[2] = (byte) ((s >> 40) & 0xff );
		bytes[3] = (byte) ((s >> 32) & 0xff );
		bytes[4] = (byte) ((s >> 24) & 0xff );
		bytes[5] = (byte) ((s >> 16) & 0xff );
		bytes[6] = (byte) ((s >> 8) & 0xff );
		bytes[7] = (byte) (s & 0xff );
		
		out.getStream().write(bytes,0,8);
	}

}
