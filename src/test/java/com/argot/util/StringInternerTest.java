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
package com.argot.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.CharBuffer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringInternerTest {

    private StringWeakInterner interner;

    @BeforeEach
    public void setup() {
        this.interner = StringWeakInterner.get();
    }

    @Test
    public void testIntern() {
        final String str = "test";
        final String result = interner.get(CharBuffer.wrap(str));
        assertNotNull(result);
        assertEquals(result, "test");
    }

    @Test
    public void testHashCollision() {
        //final String str = "test"; // teu6
        //final Random random = new Random();
        //final int hash = str.hashCode();

        String result = interner.get(CharBuffer.wrap("test"));
        assertNotNull(result);
        assertEquals(result, "test");
        result = null;

        System.gc();

        result = interner.get(CharBuffer.wrap("teu6"));
        assertNotNull(result);
        assertEquals(result, "teu6");
        result = null;

        System.gc();

        result = interner.get(CharBuffer.wrap("tg5t"));
        assertNotNull(result);
        assertEquals(result, "tg5t");
        result = null;

        System.gc();

        result = interner.get(CharBuffer.wrap("test"));
        assertNotNull(result);
        assertEquals(result, "test");
        result = null;

    }

}
