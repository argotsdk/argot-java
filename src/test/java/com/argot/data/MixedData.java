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

package com.argot.data;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

/* 
 * A simple mixed data used for testing purposes.
 */
public class MixedData {
    public static final String TYPENAME = "data";
    public static final String VERSION = "1.0";

    private final int _anInt;
    private final short _aShort;
    private final String _anUtf8;

    public MixedData(final int anInt, final short aShort, final String anUtf8) {
        _anInt = anInt;
        _aShort = aShort;
        _anUtf8 = anUtf8;
    }

    public int getInt() {
        return _anInt;
    }

    public short getShort() {
        return _aShort;
    }

    public String getString() {
        return _anUtf8;
    }

    public static class MixedDataWriter implements TypeLibraryWriter, TypeWriter {
        @Override
        public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
            final MixedData data = (MixedData) o;
            out.writeObject("uint16", Integer.valueOf(data._anInt));
            out.writeObject("uint8", Short.valueOf(data._aShort));
            out.writeObject("u8utf8", data._anUtf8);
        }

        @Override
        public TypeWriter getWriter(final TypeMap map) throws TypeException {
            return this;
        }
    }

    /*
     * This should be contained in a dictionary file instead
     * of being created in code.  Useful here for testing.
     */
    public static int register(final TypeLibrary library) throws TypeException {
        final int id = library.register(new DictionaryName(MetaName.parseName(library, TYPENAME)), new MetaIdentity());

        return library.register(new DictionaryDefinition(id, MetaName.parseName(library, TYPENAME), MetaVersion.parseVersion("1.0")),
                        new MetaSequence(new MetaExpression[] { new MetaTag("short", new MetaReference(library.getTypeId("uint16"))),
                                        new MetaTag("byte", new MetaReference(library.getTypeId("uint8"))), new MetaTag("u8utf8", new MetaReference(library.getTypeId("u8utf8"))) }),
                        new TypeReaderAuto(MixedData.class), new MixedDataWriter(), MixedData.class);
    }
}
