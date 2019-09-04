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

import java.io.ByteArrayOutputStream;
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

/*
 * An meta.envelop when called will take any object and attempt to write it out to a
 * temporary buffer.  The buffer will then be written with a size.  Size should be
 * an expression as a reference to a size like u16.  Expressions have been allowed
 * because it can be specified that way.
 */
public class MetaEnvelope extends MetaExpression implements MetaDefinition {
    public static final String TYPENAME = "meta.envelope";
    public static final String VERSION = "1.3";

    private MetaExpression _size;
    private MetaExpression _type;

    public MetaEnvelope(MetaExpression size, MetaExpression type) {
        _size = size;
        _type = type;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    @Override
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException {
        super.bind(library, definitionId, location, definition);
        _size.bind(library, definitionId, null, definition);
        _type.bind(library, definitionId, null, definition);
    }

    public MetaExpression getSizeExpression() {
        return _size;
    }

    public MetaExpression getTypeExpression() {
        return _type;
    }

    public static class MetaEnvelopTypeReader extends MetaExpressionReaderAuto implements MetaExpressionReader {
        public MetaEnvelopTypeReader() {
            super(MetaEnvelope.class);
        }

        @Override
        public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaEnvelope metaEnvelop = (MetaEnvelope) element;
            return new MetaEnvelopReader(resolver.getExpressionReader(map, metaEnvelop._size));
        }
    }

    public static class MetaEnvelopTypeWriter implements TypeLibraryWriter, TypeWriter, MetaExpressionWriter {
        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            MetaEnvelope ma = (MetaEnvelope) o;

            out.writeObject("meta.expression", ma._size);
            out.writeObject("meta.expression", ma._type);
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaEnvelope metaEnvelop = (MetaEnvelope) element;
            return new MetaEnvelopWriter(resolver.getExpressionWriter(map, metaEnvelop._type), resolver.getExpressionWriter(map, metaEnvelop._size));
        }
    }

    private static class MetaEnvelopReader implements TypeReader {
        private TypeReader _size;

        private MetaEnvelopReader(TypeReader size) {
            _size = size;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            Object sizeObject = _size.read(in);

            int size = 0;

            if (sizeObject instanceof Byte) {
                size = ((Byte) sizeObject).intValue();
            } else if (sizeObject instanceof Short) {
                size = ((Short) sizeObject).intValue();

            } else if (sizeObject instanceof Integer) {
                size = ((Integer) sizeObject).intValue();
            } else {
                throw new TypeException("meta.envelop not able to use size object");
            }

            byte[] buffer = new byte[size];
            in.read(buffer, 0, buffer.length);
            return buffer;
        }

    }

    private static class MetaEnvelopWriter implements TypeWriter {
        private TypeWriter _type;
        private TypeWriter _size;

        public MetaEnvelopWriter(TypeWriter type, TypeWriter size) {
            _type = type;
            _size = size;
        }

        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();

            TypeOutputStream tmos = new TypeOutputStream(bout, out.getTypeMap());

            _type.write(tmos, o);
            bout.close();

            byte b[] = bout.toByteArray();

            _size.write(out, Integer.valueOf(b.length));
            out.getStream().write(b);
        }

    }
}
