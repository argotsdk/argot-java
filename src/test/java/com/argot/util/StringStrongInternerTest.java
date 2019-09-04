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
import java.util.Random;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class StringStrongInternerTest {
    private StringStrongInterner interner;

    @BeforeEach
    public void setup() {
        this.interner = StringStrongInterner.getInterner("test");
    }

    @Test
    public void testEmpty() {
        final String str = "";
        String result = interner.get(CharBuffer.wrap(str));
        assertNotNull(result);
        assertEquals(result, "");

        result = interner.get(CharBuffer.wrap(str));
        assertNotNull(result);
        assertEquals(result, "");
    }

    @Test
    public void testInSameEmpty() {
        String str = "This is a test";
        CharBuffer buffer = CharBuffer.wrap(str);

        String result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);

        str = "Zrdvarksab";
        buffer = CharBuffer.wrap(str);

        result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);

        result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);
    }

    @Test
    public void testMultiple() {

        for (int x = 0; x < 1000000; x++) {
            String str = randomString();
            CharBuffer buffer = CharBuffer.wrap(str);

            // adds it.
            String result = interner.get(buffer);
            assertNotNull(result);
            assertEquals(result, str);

            // retrieves it.
            result = interner.get(buffer);
            assertNotNull(result);
            assertEquals(result, str);
        }
    }

    private Random random = new Random();

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
