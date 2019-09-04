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
package com.argot.message;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.data.MixedData;
import com.argot.data.MixedDataLoader;

public class ArgotMessageTest {
    private TypeLibrary typeLibrary;

    @BeforeEach
    public void setUp() throws TypeException {
        // Create the type library and compile/bind the switch data types.
        typeLibrary = new TypeLibrary();

        typeLibrary.loadLibrary(new MixedDataLoader());
    }

    @Test
    public void testWriteMessage() throws Exception {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ArgotMessage msg = new ArgotMessage(typeLibrary);

        msg.writeMessage(baos, MixedData.TYPENAME, new MixedData(10, (short) 50, "hello"));
        baos.flush();
        baos.close();

        final byte[] msgData = baos.toByteArray();
        System.out.println("msgSize = " + msgData.length);
        printByteData(msgData);
        final ByteArrayInputStream bais = new ByteArrayInputStream(msgData);

        final Object o = msg.readMessage(bais);

        final MixedData result = (MixedData) o;

        assertEquals(10, result.getInt());
        assertEquals(50, result.getShort());
        assertEquals("hello", result.getString());

    }

    private void printByteData(final byte[] data) {

        int count = 0;
        //System.out.println("Core Size: " + data.length);
        for (int x = 0; x < data.length; x++) {
            count++;
            if (data[x] >= 48 && data[x] <= 122) {
                final String value = String.valueOf((char) data[x]);
                System.out.print(value + "  ");
            } else {
                String value = Integer.toString(data[x], 16);
                if (value.length() == 1) {
                    value = "0" + value;
                }
                value = "" + value;

                System.out.print("" + value + " ");
            }
            if (count > 23) {
                count = 0;
                System.out.println("");
            }
        }

        System.out.println("");
    }

}
