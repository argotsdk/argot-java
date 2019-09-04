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
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;

public class TwoWayHashMapTest

{

    @Test
    public void testAddItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(1, 1, Integer.valueOf(1));
    }

    @Test
    public void testAddDuplicateKeyItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(1, 1, Integer.valueOf(1));
        try {
            map.add(1, 2, Integer.valueOf(1));
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            //ignore.
        }
    }

    @Test
    public void testAddNullItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        try {
            map.add(2, 1, null);
            fail("should throw exception");
        } catch (IllegalArgumentException e) {
            //ignore.
        }
    }

    @Test
    public void testGetItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(100, 50, Integer.valueOf(1));
        int x = map.findValue(100);
        assertEquals(x, 50);
    }

    @Test
    public void testGetReverseItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(100, 50, Integer.valueOf(1));
        int x = map.findKey(50);
        assertEquals(x, 100);
    }

    @Test
    public void testRemoveItem() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(100, 50, Integer.valueOf(1));
        map.remove(100);
        Object o = map.getObjectFromKey(100);
        assertEquals(o, null);
    }

    @Test
    public void testRemoveInvalid() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(100, 50, Integer.valueOf(1));
        // returns null if its invalid.
        map.remove(10);
    }

    @Test
    public void testGetNotFound() {
        TwoWayHashMap map = new TwoWayHashMap();
        map.add(100, 50, Integer.valueOf(1));
        int x = map.findValue(10);
        assertEquals(x, -1);
    }
}
