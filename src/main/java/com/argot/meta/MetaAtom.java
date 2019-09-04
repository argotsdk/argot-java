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
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;

public class MetaAtom extends MetaExpression implements MetaDefinition {
    public static String TYPENAME = "meta.atom";
    public static final String VERSION = "1.3";

    private int _min_bit_length;
    private int _max_bit_length;
    private MetaAtomAttribute[] _attributes;

    public MetaAtom(int min_bit_length, int max_bit_length, MetaAtomAttribute[] attributes) {
        _min_bit_length = min_bit_length;
        _max_bit_length = max_bit_length;
        _attributes = attributes;
    }

    public MetaAtom(int min_bit_length, int max_bit_length, Object[] attributes) {
        _min_bit_length = min_bit_length;
        _max_bit_length = max_bit_length;

        _attributes = new MetaAtomAttribute[attributes.length];
        for (int x = 0; x < attributes.length; x++) {
            _attributes[x] = (MetaAtomAttribute) attributes[x];
        }

    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    public static class MetaBasicTypeWriter implements TypeLibraryWriter, TypeWriter {
        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            MetaAtom tb = (MetaAtom) o;

            out.writeObject(UVInt28.TYPENAME, Integer.valueOf(tb._min_bit_length));
            out.writeObject(UVInt28.TYPENAME, Integer.valueOf(tb._max_bit_length));

            // This could be replaced with an array marshaller.
            out.writeObject(UInt8.TYPENAME, Short.valueOf((short) tb._attributes.length));
            for (int x = 0; x < tb._attributes.length; x++) {
                out.writeObject(MetaAtomAttribute.TYPENAME, tb._attributes[x]);
            }
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }
    }

}
