/*
 * Copyright (c) 2003-2017, Live Media Pty. Ltd.
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
package com.argot.auto;

import java.io.OutputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

import com.argot.TypeException;
import com.argot.TypeOutputStream;

public interface MethodHandleWriter
{
	void write(Object o, TypeOutputStream out) throws Throwable;

	public static MethodHandleWriter getWriter(final Method method, final String argotType, final boolean writeNotNull) throws IllegalAccessException
	{
		MethodHandleWriter writer = null;

		final Class<?> returnType = method.getReturnType();
		if (returnType == boolean.class && "boolean".equals(argotType))
		{
			writer = new BooleanMethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}
		else if ((returnType == byte.class || returnType == short.class || returnType == int.class) && "uint8".equals(argotType))
		{
			writer = new UInt8MethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}
		else if (returnType == short.class && "int16".equals(argotType))
		{
			writer = new Int16MethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}
		else if (returnType == int.class && "int32".equals(argotType))
		{
			writer = new Int32MethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}
		else if (returnType == long.class && "int64".equals(argotType))
		{
			writer = new Int64MethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}
		else if (returnType == String.class && "u8utf8".equals(argotType))
		{
			writer = new U8Utf8MethodHandleWriter(MethodHandles.lookup().unreflect(method), writeNotNull);
		}

		return writer;
	}
	public static abstract class AbstractWriter implements MethodHandleWriter
	{
		protected final MethodHandle getHandle;
		protected final boolean writeNotNull;

		public AbstractWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			this.getHandle = getHandle;
			this.writeNotNull = writeNotNull;
		}
	}

	public static final class BooleanMethodHandleWriter extends AbstractWriter
	{
		public BooleanMethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			final boolean b = (boolean) getHandle.invoke(o);
			final OutputStream os = out.getStream();
			if (writeNotNull)
			{
				os.write(1);
			}
			os.write((b == true ? 1 : 0));
		}
	}

	public static final class UInt8MethodHandleWriter extends AbstractWriter
	{
		public UInt8MethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			final int i = (int) getHandle.invoke(o);
			final OutputStream os = out.getStream();
			if (writeNotNull)
			{
				os.write(1);
			}

			os.write(i & 0xff);
		}
	}

	public static final class Int16MethodHandleWriter extends AbstractWriter
	{
		public Int16MethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			final short s = (short) getHandle.invoke(o);
			final OutputStream os = out.getStream();

			if (writeNotNull)
			{
				os.write(1);
			}

			os.write((s >> 8) & 0xff);
			os.write(s & 0xff);
		}
	}

	public static final class Int32MethodHandleWriter extends AbstractWriter
	{
		public Int32MethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			final int s = (int) getHandle.invoke(o);
			final OutputStream os = out.getStream();

			if (writeNotNull)
			{
				os.write(1);
			}

			os.write((byte) ((s >> 24) & 0xff));
			os.write((byte) ((s >> 16) & 0xff));
			os.write((byte) ((s >> 8) & 0xff));
			os.write((byte) (s & 0xff));
		}
	}

	public static final class Int64MethodHandleWriter extends AbstractWriter
	{
		public Int64MethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			final long s = (long) getHandle.invoke(o);
			final OutputStream os = out.getStream();

			if (writeNotNull)
			{
				os.write(1);
			}

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

	public static final class StringBuffers
	{
		ByteBuffer buffer = ByteBuffer.allocate(512);
		CharsetEncoder encoder = Charset.forName("UTF8").newEncoder();
	}

	public static final class U8Utf8MethodHandleWriter extends AbstractWriter
	{
		private final ThreadLocal<StringBuffers> buffers = new ThreadLocal<StringBuffers>()
		{
			@Override
			public StringBuffers initialValue()
			{
				return new StringBuffers();
			}
		};

		public U8Utf8MethodHandleWriter(final MethodHandle getHandle, final boolean writeNotNull)
		{
			super(getHandle, writeNotNull);
		}

		@Override
		public void write(final Object o, final TypeOutputStream out) throws Throwable
		{
			// finally set the string value.
			final String str = (String) getHandle.invoke(o);

			final OutputStream os = out.getStream();

			// empty strings just write 0 for null.
			if (str == null)
			{
				os.write(0);
				return;
			}

			// Grab a thread local set of buffers to use temporarily.
			final StringBuffers buf = buffers.get();

			// get a reference to the buffers.
			final ByteBuffer b = buf.buffer;

			// this does allocate an object, but at least it isn't copying the buffer!
			final CharBuffer c = CharBuffer.wrap(str);

			// clear the byte buffer.
			b.clear();

			// decode the bytes into the char buffer.
			final CharsetEncoder encoder = buf.encoder;
			encoder.reset();
			encoder.encode(c, b, true);

			// flip the char buffer.
			b.flip();

			final int size = b.limit();

			if (size > 255)
			{
				throw new TypeException("u8ascii: String length exceeded max length of 255.  len =" + size);
			}

			if (writeNotNull)
			{
				os.write(1);
			}

			os.write(size);
			os.write(b.array(), 0, size);
		}
	}
}
