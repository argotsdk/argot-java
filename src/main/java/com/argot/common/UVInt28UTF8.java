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
 * This is a short string encoded in UTF8 format. It uses a single unsigned byte to specify the length. So the data
 * length can be between 0-254 characters. 0 is an empty string. 255 is a null string.
 */
public class UVInt28UTF8 implements TypeReader, TypeWriter {
    public static final String TYPENAME = "u32utf8";
    public static final String VERSION = "1.3";

    @Override
    public Object read(final TypeInputStream in) throws TypeException, IOException {
        final Integer id = (Integer) in.readObject(UVInt28.TYPENAME);

        String v = null;

        if (id.intValue() > 0) {
            final byte[] bytes = new byte[id.intValue()];
            final int read = in.read(bytes, 0, bytes.length);
            v = new String(bytes, 0, read, "UTF-8");
        } else {
            v = new String("");
        }

        return v;
    }

    @Override
    public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
        if (!(o instanceof String)) {
            if (o == null) {
                out.writeObject(UVInt28.TYPENAME, Integer.valueOf(0));
                return;
            }
            throw new TypeException("StringType: can only write objects of type String");
        }

        final String s = (String) o;
        final byte[] bytes = s.getBytes();
        final int len = bytes.length;

        out.writeObject(UVInt28.TYPENAME, Integer.valueOf(len));
        out.getStream().write(bytes, 0, len);
    }
}
