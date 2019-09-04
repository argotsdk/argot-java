/*
 * Copyright (c) 2003-2019, Live Media Pty. Ltd.
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
import java.io.OutputStream;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a basic 32-bit signed int value stored in big endian format, two's compliment.
 */
public class Int32 implements TypeReader, TypeWriter {
    public static final String TYPENAME = "int32";
    public static final String VERSION = "1.3";

    @Override
    public Object read(final TypeInputStream in) throws TypeException, IOException {
        // byte bytes[] = new byte[4];
        // in.read(bytes,0,4);

        // reducing heap pressure by reading a byte at a time. Might be slightly heavier on CPU.
        final int value = (((in.read() & 0xff) << 24) | ((in.read() & 0xff) << 16) | ((in.read() & 0xff) << 8) | (in.read() & 0xff));

        // need to return a long value here because an unsigned
        // integer can be bigger than the java int.
        return Integer.valueOf(value);
    }

    @Override
    public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
        if (!(o instanceof Integer)) {
            throw new TypeException("s32: requires Integer");
        }

        final int s = ((Integer) o).intValue();

        if (s < MIN || s > MAX) {
            throw new TypeException("U16B: value out of range" + s);
        }

        // This is going to turn our signed value into a unsigned
        // 16 bits.
        // byte[] bytes = new byte[4];

        // write individual bytes to reduce heap pressue.
        final OutputStream os = out.getStream();

        os.write((byte) ((s >> 24) & 0xff));
        os.write((byte) ((s >> 16) & 0xff));
        os.write((byte) ((s >> 8) & 0xff));
        os.write((byte) (s & 0xff));

        // out.getStream().write(bytes,0,4);
    }

    public final long MIN = -2147483648; // -2^31;
    public final long MAX = 2147483647; // 2^31-1;
}
