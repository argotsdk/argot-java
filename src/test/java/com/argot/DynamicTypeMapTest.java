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

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.argot.common.CommonLoader;
import com.argot.meta.MetaLoader;
import com.argot.meta.MetaSequence;

public class DynamicTypeMapTest

{
    TypeLibrary _library;
    TypeMap _typeMap;

    TypeLibraryLoader libraryLoaders[] = { new MetaLoader(), new CommonLoader() };

    @BeforeEach
    protected void setUp() throws Exception {

        _library = new TypeLibrary(libraryLoaders);
        _typeMap = new TypeMap(_library, new TypeMapperDynamic(new TypeMapperError()));
    }

    public void testMap() throws Exception {
        _typeMap.map(1, _library.getDefinitionId("uint8", "1.3"));
    }

    @Test
    public void testGetIdName() throws Exception {
        int id = _typeMap.getStreamId("uint8");
        assertTrue(id != TypeMap.NOTYPE);
    }

    @Test
    public void testGetIdSystemId() throws Exception {
        int id = _typeMap.getStreamId(_library.getDefinitionId("uint8", "1.3"));
        assertTrue(id != TypeMap.NOTYPE);
    }

    @Test
    public void testGetIdClass() throws Exception {
        int[] ids = _typeMap.getStreamId(MetaSequence.class);
        assertTrue(ids.length == 1);
        assertTrue(ids[0] != TypeMap.NOTYPE);
    }

    @Test
    public void testGetReader() throws Exception {
        TypeReader reader = _typeMap.getReader(_typeMap.getStreamId("uint8"));
        assertNotNull(reader);
    }

    @Test
    public void testGetWriter() throws Exception {
        TypeWriter writer = _typeMap.getWriter(_typeMap.getStreamId("uint8"));
        assertNotNull(writer);
    }
    /*
    	public void testIsValidName()
    	throws Exception
    	{
    		boolean valid = _typeMap.isValid( "u8" );
    		assertTrue( valid );
    	}
    */
}
