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

import java.io.InputStream;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;

import com.argot.TypeInputStream;
import com.argot.util.StringStrongInterner;
import com.argot.util.StringWeakInterner;

public interface MethodHandleReader {
    void read(Object o, TypeInputStream in) throws Throwable;

    public static MethodHandleReader getReader(final Method method, final String argotType) throws IllegalAccessException {
        MethodHandleReader reader = null;

        final Class<?> returnType = method.getParameters()[0].getType();
        if (returnType == boolean.class && "boolean".equals(argotType)) {
            reader = new BooleanMethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if ((returnType == short.class || returnType == int.class) && "uint8".equals(argotType)) {
            reader = new UInt8MethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == short.class && "int16".equals(argotType)) {
            reader = new Int16MethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == int.class && "int32".equals(argotType)) {
            reader = new Int32MethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == float.class && "float".equals(argotType)) {
            reader = new IEEEFloatMethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == long.class && "int64".equals(argotType)) {
            reader = new Int64MethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == double.class && "double".equals(argotType)) {
            reader = new IEEEDoubleMethodHandleReader(MethodHandles.lookup().unreflect(method));
        } else if (returnType == String.class && "u8utf8".equals(argotType)) {
            final ArgotIntern intern = method.getAnnotation(ArgotIntern.class);
            if (intern != null) {
                if ("weak".equalsIgnoreCase(intern.value())) {
                    reader = new U8Utf8WeakInternMethodHandleReader(MethodHandles.lookup().unreflect(method));
                } else {
                    reader = new U8Utf8StrongInternMethodHandleReader(MethodHandles.lookup().unreflect(method), intern.value());
                }
            } else {
                reader = new U8Utf8MethodHandleReader(MethodHandles.lookup().unreflect(method));
            }
        }

        return reader;
    }

    public static abstract class AbstractReader implements MethodHandleReader {

        protected final MethodHandle setHandle;

        public AbstractReader(final MethodHandle setHandle) {
            this.setHandle = setHandle;
        }
    }

    public static final class BooleanMethodHandleReader extends AbstractReader {
        public BooleanMethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final boolean a = (in.read() == 0 ? false : true);
            setHandle.invoke(o, a);
        }
    }

    public static final class UInt8MethodHandleReader extends AbstractReader {
        public UInt8MethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final short value = (short) in.getStream().read();
            setHandle.invoke(o, value);
        }
    }

    public static final class Int16MethodHandleReader extends AbstractReader {
        public Int16MethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final short value = (short) (((in.read() & 0xff) << 8) | (in.read() & 0xff));
            setHandle.invoke(o, value);
        }
    }

    public static final class Int32MethodHandleReader extends AbstractReader {
        public Int32MethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final int value = (((in.read() & 0xff) << 24) | ((in.read() & 0xff) << 16) | ((in.read() & 0xff) << 8) | (in.read() & 0xff));
            setHandle.invoke(o, value);
        }
    }

    public static final class IEEEFloatMethodHandleReader extends AbstractReader {
        public IEEEFloatMethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final int value = (((in.read() & 0xff) << 24) | ((in.read() & 0xff) << 16) | ((in.read() & 0xff) << 8) | (in.read() & 0xff));
            setHandle.invoke(o, Float.intBitsToFloat(value));
        }
    }

    public static final class Int64MethodHandleReader extends AbstractReader {
        public Int64MethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final InputStream is = in.getStream();

            final long value = ((((long) is.read() & 0xff) << 56) | (((long) is.read() & 0xff) << 48) | (((long) is.read() & 0xff) << 40) | (((long) is.read() & 0xff) << 32)
                            | (((long) is.read() & 0xff) << 24) | (((long) is.read() & 0xff) << 16) | (((long) is.read() & 0xff) << 8) | ((long) is.read() & 0xff));

            setHandle.invoke(o, value);
        }
    }

    public static final class IEEEDoubleMethodHandleReader extends AbstractReader {
        public IEEEDoubleMethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final InputStream is = in.getStream();

            final long value = ((((long) is.read() & 0xff) << 56) | (((long) is.read() & 0xff) << 48) | (((long) is.read() & 0xff) << 40) | (((long) is.read() & 0xff) << 32)
                            | (((long) is.read() & 0xff) << 24) | (((long) is.read() & 0xff) << 16) | (((long) is.read() & 0xff) << 8) | ((long) is.read() & 0xff));

            setHandle.invoke(o, Double.longBitsToDouble(value));
        }
    }

    public static final class StringBuffers {
        ByteBuffer buffer = ByteBuffer.allocate(512);
        CharBuffer charBuffer = CharBuffer.allocate(300);
        CharsetDecoder decoder = Charset.forName("UTF8").newDecoder();
    }

    public static final class U8Utf8MethodHandleReader extends AbstractReader {
        private final ThreadLocal<StringBuffers> buffers = new ThreadLocal<StringBuffers>() {
            @Override
            public StringBuffers initialValue() {
                return new StringBuffers();
            }
        };

        public U8Utf8MethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final int len = in.read();

            // Grab a thread local set of buffers to use temporarily.
            final StringBuffers buf = buffers.get();

            // get a reference to the buffers.
            final ByteBuffer b = buf.buffer;
            final CharBuffer c = buf.charBuffer;

            b.clear();
            c.clear();

            // read the stream into the byte buffer.
            in.getStream().read(b.array(), 0, len);
            b.limit(len);

            // decode the bytes into the char buffer.
            final CharsetDecoder decoder = buf.decoder;
            decoder.reset();
            decoder.decode(b, c, true);

            // flip the char buffer.
            c.flip();

            // get a copy of
            final String str = c.toString();

            // finally set the string value.
            setHandle.invoke(o, str);
        }
    }

    public static final class U8Utf8WeakInternMethodHandleReader extends AbstractReader {
        private final ThreadLocal<StringBuffers> buffers = new ThreadLocal<StringBuffers>() {
            @Override
            public StringBuffers initialValue() {
                return new StringBuffers();
            }
        };

        private final StringWeakInterner interner;

        public U8Utf8WeakInternMethodHandleReader(final MethodHandle setHandle) {
            super(setHandle);

            interner = StringWeakInterner.get();
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final int len = in.read();

            // Grab a thread local set of buffers to use temporarily.
            final StringBuffers buf = buffers.get();

            // get a reference to the buffers.
            final ByteBuffer b = buf.buffer;
            final CharBuffer c = buf.charBuffer;

            b.clear();
            c.clear();

            // read the stream into the byte buffer.
            in.getStream().read(b.array(), 0, len);
            b.limit(len);

            // decode the bytes into the char buffer.
            final CharsetDecoder decoder = buf.decoder;
            decoder.reset();
            decoder.decode(b, c, true);

            // flip the char buffer.
            c.flip();

            // get a copy of
            final String str = interner.get(c);

            // finally set the string value.
            setHandle.invoke(o, str);
        }
    }

    public static final class U8Utf8StrongInternMethodHandleReader extends AbstractReader {
        private final StringStrongInterner interner;

        private final ThreadLocal<StringBuffers> buffers = new ThreadLocal<StringBuffers>() {
            @Override
            public StringBuffers initialValue() {
                return new StringBuffers();
            }
        };

        public U8Utf8StrongInternMethodHandleReader(final MethodHandle setHandle, final String name) {
            super(setHandle);
            interner = StringStrongInterner.getInterner(name);
        }

        @Override
        public void read(final Object o, final TypeInputStream in) throws Throwable {
            final int len = in.read();

            // Grab a thread local set of buffers to use temporarily.
            final StringBuffers buf = buffers.get();

            // get a reference to the buffers.
            final ByteBuffer b = buf.buffer;
            final CharBuffer c = buf.charBuffer;

            b.clear();
            c.clear();

            // read the stream into the byte buffer.
            in.getStream().read(b.array(), 0, len);
            b.limit(len);

            // decode the bytes into the char buffer.
            final CharsetDecoder decoder = buf.decoder;
            decoder.reset();
            decoder.decode(b, c, true);

            // flip the char buffer.
            c.flip();

            // get a copy of
            final String str = interner.get(c);

            // finally set the string value.
            setHandle.invoke(o, str);
        }
    }
}
