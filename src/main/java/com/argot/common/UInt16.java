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

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a basic 16-bit unsigned int value stored in big endian format. Reads and writes Integer type.
 */
public class UInt16 implements TypeReader, TypeWriter {
    public static final String TYPENAME = "uint16";
    public static final String VERSION = "1.3";

    @Override
    public Object read(TypeInputStream in) throws TypeException, IOException {
        byte[] buffer = new byte[2];

        in.read(buffer, 0, 2);
        return Integer.valueOf(((buffer[0] & 0xff) << 8) | (buffer[1] & 0xff));
    }

    @Override
    public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
        byte[] buffer = new byte[2];
        int s;

        if (o instanceof Integer) {
            s = ((Integer) o).intValue();
        } else if (o instanceof Short) {
            s = ((Short) o).intValue();
        } else
            throw new TypeException("BigEndianShort requires Short or Integer object");

        if (s < MIN || s > MAX)
            throw new TypeException("u16: value out of range:" + s);

        buffer[0] = (byte) ((s >> 8) & 0xff);
        buffer[1] = (byte) (s & 0xff);
        out.getStream().write(buffer, 0, 2);
    }

    public static final int MIN = 0;
    public static final int MAX = 65536; //2^16-1; 
}
