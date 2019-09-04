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

import java.io.IOException;
import java.io.InputStream;

/**
 * This is an InputStream helper which reads objects directly off the stream.
 */
public class TypeInputStream {
    private TypeMap _map;
    private InputStream _in;

    public TypeInputStream(InputStream in, TypeMap map) {
        _in = in;
        _map = map;
    }

    public InputStream getStream() {
        return _in;
    }

    public TypeMap getTypeMap() {
        return _map;
    }

    public void setTypeMap(TypeMap map) {
        _map = map;
    }

    public Object readObject(int streamId) throws TypeException, IOException {
        TypeReader reader = _map.getReader(streamId);

        try {
            return reader.read(this);
        } catch (TypeStreamException readEx) {
            readEx.addTypeName(_map.getName(streamId).toString());
            throw readEx;
        } catch (IOException ioEx) {
            throw new TypeStreamException(_map.getName(streamId).getFullName(), ioEx);
        }
    }

    public Object readObject(String name) throws TypeException, IOException {
        int id = _map.getStreamId(name);
        if (id == TypeLibrary.NOTYPE)
            throw new TypeException("type not registered");

        TypeReader reader = _map.getReader(id);

        try {
            return reader.read(this);
        } catch (TypeStreamException readEx) {
            readEx.addTypeName(_map.getName(id).toString());
            throw readEx;
        } catch (IOException ioEx) {
            throw new TypeStreamException(_map.getName(id).getFullName(), ioEx);
        }
    }

    /**
     * Helper method that will throw an exception if the input stream closes.
     * 
     * @return
     * @throws TypeException
     * @throws IOException
     */
    public int read() throws TypeException, IOException {
        int b = _in.read();
        if (b == -1)
            throw new TypeException("TypeInputStream: input stream closed");
        return b;
    }

    /**
     * Helper method that will throw an exception if all the bytes are not read. Required because InputStream.read can
     * return without filling the full buffer.
     * 
     * @param buffer
     * @param offset
     * @param count
     * @return
     * @throws TypeException
     * @throws IOException
     */
    public int read(byte[] buffer, int offset, int count) throws TypeException, IOException {
        int read = 0;
        while (count > 0) {
            read += _in.read(buffer, offset, count);
            count -= read;
            offset += read;
        }
        return read;
    }
}
