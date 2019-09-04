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

/*
 * UVarInt.  Unsigned Variable Length Integer.
 * 
 * Encodes a variable length unsigned integer up to 2^28.  Uses a common technique of using
 * the MSB as a flag to indicate a continuation. This version will decode a maximum
 * of four bytes.
 * 
 * A value between 0-127 will be encoded in a single byte.  128 to 2^14 will be encoded in
 * two bytes.
 * 
 * (meta.entry
 * 		(library.definition meta.name:"uvarint" meta.version:"1.0")
 *      (meta.atomic [
 *      	(meta.atomic.integer)
 *      	(meta.atomic.unsigned max_base:"28")
 *      ])
 * 
 */

public class UVInt28 implements TypeReader, TypeWriter {
    public static final String TYPENAME = "uvint28";
    public static final String VERSION = "1.3";

    @Override
    public Object read(TypeInputStream in) throws TypeException, IOException {
        int a;
        int value = 0;

        a = in.getStream().read();

        while (true) {
            value = value + (a & 0x7F);

            if ((a & 0x80) > 1) {
                value <<= 7;

                a = in.getStream().read();
                if (a == -1) {
                    throw new IOException("End of stream");
                }
            } else {
                break;
            }
        }

        return Integer.valueOf(value);
    }

    @Override
    public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
        int s;

        if (o instanceof Long) {
            s = ((Long) o).intValue();
        } else if (o instanceof Integer) {
            s = ((Integer) o).intValue();
        } else {
            throw new TypeException("uvint28: requires Integer or Long");
        }

        if (s < 0 || s > ((1 << 28) - 1)) {
            throw new TypeException("uvint28: value out of range - " + s);
        }

        // if the value is small write single byte and finish.
        // otherwise more complex encoding is required.
        if (s < 128) {
            out.getStream().write(s);
        } else {
            int shift = 21;
            boolean found = false;

            while (shift > 0) {
                int mask = 0x7f << shift;
                int value = (s & mask) >> shift;
                if (value > 0 || found) {
                    value |= 0x80;

                    found = true;
                    //System.out.println("Writing Value: " + Integer.toBinaryString(value));
                    out.getStream().write(value);
                }

                shift -= 7;
            }

            //System.out.println("Writing Value: " + Integer.toBinaryString(s & 0x7F));
            out.getStream().write(s & 0x7F);
        }

    }

}
