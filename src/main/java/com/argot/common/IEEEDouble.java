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
import java.io.InputStream;
import java.io.OutputStream;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class IEEEDouble
{
	public static final String TYPENAME = "double";
	public static final String VERSION = "1.3";

	public static class Reader implements TypeReader
	{
		@Override
		public Object read(final TypeInputStream in) throws TypeException, IOException
		{
			final InputStream is = in.getStream();

			final long value = ((((long) is.read() & 0xff) << 56) | (((long) is.read() & 0xff) << 48) | (((long) is.read() & 0xff) << 40) | (((long) is.read() & 0xff) << 32)
					| (((long) is.read() & 0xff) << 24) | (((long) is.read() & 0xff) << 16) | (((long) is.read() & 0xff) << 8) | ((long) is.read() & 0xff));

			return new Double(Double.longBitsToDouble(value));
		}
	}

	public static class Writer implements TypeWriter
	{
		@Override
		public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException
		{
			if (!(o instanceof Double))
			{
				throw new TypeException("ieee.double: requires Double");
			}

			final Double dble = ((Double) o);
			final long s = Double.doubleToRawLongBits(dble.doubleValue());

			final OutputStream os = out.getStream();
			os.write((byte) ((s >> 56) & 0xff));
			os.write((byte) ((s >> 48) & 0xff));
			os.write((byte) ((s >> 40) & 0xff));
			os.write((byte) ((s >> 32) & 0xff));
			os.write((byte) ((s >> 24) & 0xff));
			os.write((byte) ((s >> 16) & 0xff));
			os.write((byte) ((s >> 8) & 0xff));
			os.write((byte) (s & 0xff));

		}
	}

}
