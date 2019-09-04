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
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.junit.jupiter.api.Test;

public class ChunkByteBufferTest {
    @Test
    public void testGetInputStream() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        try {
            buffer.getInputStream();
            fail("input before output");
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    public void testGetOutputStream() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        assertNotNull(out);
    }

    @Test
    public void testGetOutputSecond() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        assertNotNull(out);
        try {
            buffer.getOutputStream();
            fail("should fail");
        } catch (IOException e) {
            // ignore
        }

    }

    @Test
    public void testWriteOut() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        out.write("some data".getBytes());
    }

    @Test
    public void testWriteSingleChar() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        out.write(56);
    }

    @Test
    public void testWriteAfterClose() throws Exception {
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        out.write("some data".getBytes());
        out.close();
        try {
            out.write("some data".getBytes());
            fail("write after close");
        } catch (IOException e) {
            // ignore
        }
    }

    @Test
    public void testWriteAndRead() throws Exception {
        String test = "some data";
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        out.write(test.getBytes());
        out.close();

        byte[] data = new byte[100];
        InputStream in = buffer.getInputStream();
        int r = in.read(data);
        assertEquals(new String(data, 0, r), test);
    }

    @Test
    public void testWriteAndReadSingleChar() throws Exception {
        String test = "some data";
        ChunkByteBuffer buffer = new ChunkByteBuffer();
        OutputStream out = buffer.getOutputStream();
        out.write(test.getBytes());
        out.close();

        InputStream in = buffer.getInputStream();
        int r = in.read();
        assertEquals(r, test.charAt(0));
    }
}
