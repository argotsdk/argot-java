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
 * This is a byte array. Basically for any binary data or storing other types of data as part of another mapped set. It uses a single unsigned 32bit integer to specificy the size of the array. It returns and writes byte[].
 */
public class UVInt28Binary implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "uvint28binary";
	public static final String VERSION = "1.3";

	public Object read(final TypeInputStream in) throws TypeException, IOException
	{
		final Integer id = (Integer) in.readObject(UVInt28.TYPENAME);

		final byte[] bytes = new byte[id.intValue()];
		in.read(bytes, 0, bytes.length);

		return bytes;
	}

	public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException
	{
		if (o instanceof byte[])
		{
			final byte[] bytes = (byte[]) o;

			out.writeObject(UVInt28.TYPENAME, new Integer(bytes.length));
			out.getStream().write(bytes);
		}
		else
		{
			throw new TypeException("uvint28binary: can only write objects of type byte[]");
		}

	}
}
