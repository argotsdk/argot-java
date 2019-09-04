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
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt8;

public class MetaSequence extends MetaExpression implements MetaDefinition {
    public static final String TYPENAME = "meta.sequence";
    public static final String VERSION = "1.3";

    private MetaExpression[] _objects;

    public MetaSequence(Object[] objects) {
        if (objects == null)
            throw new IllegalArgumentException("MetaSequence: object list null");

        _objects = new MetaExpression[objects.length];

        for (int x = 0; x < objects.length; x++) {
            if (objects[x] == null)
                throw new IllegalArgumentException("MetaSequence: object[" + x + "] is null");
            _objects[x] = (MetaExpression) objects[x];
        }
    }

    public MetaSequence(MetaExpression[] objects) {
        if (objects == null)
            throw new IllegalArgumentException("MetaSequence: objects list null");

        for (int x = 0; x < objects.length; x++) {
            if (objects[x] == null)
                throw new IllegalArgumentException("MetaSequence: object[" + x + "] is null");
        }

        _objects = objects;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    @Override
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException {
        super.bind(library, definitionId, location, definition);
        for (int x = 0; x < _objects.length; x++) {
            _objects[x].bind(library, definitionId, null, definition);
        }
    }

    public MetaExpression getElement(int x) {
        return _objects[x];
    }

    public int size() {
        return _objects.length;
    }

    public static class MetaSequenceTypeReader extends MetaExpressionReaderAuto implements MetaExpressionReader {
        public MetaSequenceTypeReader() {
            super(MetaSequence.class);
        }

        @Override
        public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaSequence sequence = (MetaSequence) element;

            TypeReader[] readers = new TypeReader[sequence.size()];
            for (int x = 0; x < sequence.size(); x++) {
                readers[x] = resolver.getExpressionReader(map, sequence.getElement(x));
            }
            return new MetaSequenceReader(readers);
        }
    }

    private static class MetaSequenceReader implements TypeReader {
        private TypeReader[] _readers;

        public MetaSequenceReader(TypeReader[] readers) {
            _readers = readers;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            Object[] objects = new Object[_readers.length];

            for (int x = 0; x < _readers.length; x++) {

                objects[x] = _readers[x].read(in);
            }
            return objects;
        }

    }

    public static class MetaSequenceTypeWriter implements TypeLibraryWriter, TypeWriter {
        @Override
        public void write(TypeOutputStream out, Object obj) throws TypeException, IOException {
            MetaSequence ts = (MetaSequence) obj;

            out.writeObject(UInt8.TYPENAME, Integer.valueOf(ts._objects.length));

            for (int x = 0; x < ts._objects.length; x++) {
                Object o = ts._objects[x];
                out.writeObject("meta.expression", o);
            }
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }

    }

    public static class MetaSequenceReaderWalker {
        TypeReader getReader(TypeMap map, MetaSequence sequence) {
            return null;
        }
    }

    public TypeWriter getWriter(TypeMap map) throws TypeException {
        throw new TypeException("not implemented");
    }
}