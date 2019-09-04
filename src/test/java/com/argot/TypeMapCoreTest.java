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
package com.argot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.dictionary.Dictionary;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class TypeMapCoreTest extends TestCase {
    private TypeLibrary _library;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _library = new TypeLibrary(false);
        _library.loadLibrary(new MetaLoader());
    }

    public void testTypeMapCore() throws Exception {
        TypeMap baseMap = new TypeMap(_library, new TypeMapperCore(new TypeMapperError()));
        TypeMap coreMap = new TypeMap(_library, new TypeMapperCore(new TypeMapperError()));
        coreMap.setReference(TypeMap.REFERENCE_MAP, baseMap);

        byte[] core = writeCore(coreMap);

        int count = 0;
        System.out.println("Core Size: " + core.length);
        for (int x = 0; x < core.length; x++) {
            count++;

            if (core[x] >= 48 && core[x] <= 122) {
                String value = String.valueOf((char) core[x]);
                System.out.print(value + "  ");
            } else {
                String value = Integer.toString(core[x], 16);
                if (value.length() == 1)
                    value = "0" + value;
                value = "" + value;

                System.out.print("" + value + " ");
            }
            if (count > 23) {
                count = 0;
                System.out.println("");
            }
        }

        int zeros = 0;
        for (int x = 0; x < core.length; x++) {
            if (core[x] == 0)
                zeros++;
        }
        System.out.println("\n\nZeros:" + zeros);
    }

    public void testGetClass() throws Exception {
        // TypeMap map = TypeMapCore.getCoreTypeMap( _library );

        //  int id = map.getStreamId( MetaFixedWidth.class );
        //  assertEquals( id, map.getStreamId( MetaFixedWidth.TYPENAME ));
    }

    public static byte[] writeCore(TypeMap map) throws TypeException, IOException {
        TypeMap refCore = new TypeMap(map.getLibrary(), new TypeMapperCore(new TypeMapperError()));
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        TypeOutputStream coreObjectStream = new TypeOutputStream(baos1, map);
        coreObjectStream.writeObject(Dictionary.DICTIONARY_ENTRY_LIST, refCore);
        baos1.close();
        return baos1.toByteArray();
    }

}
