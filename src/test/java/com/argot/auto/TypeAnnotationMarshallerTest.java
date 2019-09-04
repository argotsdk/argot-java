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

package com.argot.auto;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.common.CommonLoader;
import com.argot.data.MixedDataAnnotated;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class TypeAnnotationMarshallerTest extends TestCase {
    private TypeLibrary _library;

    TypeLibraryLoader libraryLoaders[] = { new MetaLoader(), new CommonLoader(), };

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _library = new TypeLibrary(libraryLoaders);
        MixedDataAnnotated.register(_library);
    }

    public void testTypeMapCore() throws Exception {
        MixedDataAnnotated data = new MixedDataAnnotated(2345, (short) 234, "Testing");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        TypeMap map = new TypeMap(_library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));
        TypeOutputStream out = new TypeOutputStream(baos, map);
        out.writeObject(MixedDataAnnotated.TYPENAME, data);
        baos.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        TypeInputStream in = new TypeInputStream(bais, map);
        Object o = in.readObject(MixedDataAnnotated.TYPENAME);
        assertEquals(o.getClass(), data.getClass());
        MixedDataAnnotated readData = data;
        assertEquals(readData.getInt(), data.getInt());
        assertEquals(readData.getShort(), data.getShort());
        assertEquals(readData.getString(), data.getString());

    }
}
