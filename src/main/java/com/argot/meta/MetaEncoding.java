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
import com.argot.common.U8Utf8;

public class MetaEncoding extends MetaExpression implements MetaDefinition {
    public static final String TYPENAME = "meta.encoding";
    public static final String VERSION = "1.3";

    private MetaExpression _expression;
    private String _encoding;

    public MetaEncoding(MetaExpression expression, String encoding) {
        _expression = expression;
        _encoding = encoding;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    @Override
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException {
        super.bind(library, definitionId, location, definition);
        _expression.bind(library, definitionId, null, definition);
    }

    public static class MetaEncodingTypeReader extends MetaExpressionReaderAuto implements MetaExpressionReader {
        public MetaEncodingTypeReader() {
            super(MetaEncoding.class);
        }

        @Override
        public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaEncoding metaEncoding = (MetaEncoding) element;
            return new MetaEncodingReader(resolver.getExpressionReader(map, metaEncoding._expression), metaEncoding._encoding);
        }
    }

    public static class MetaEncodingTypeWriter implements TypeLibraryWriter, TypeWriter, MetaExpressionWriter {
        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            MetaEncoding enc = (MetaEncoding) o;

            out.writeObject(MetaExpression.TYPENAME, enc._expression);
            out.writeObject(U8Utf8.TYPENAME, enc._encoding);
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            throw new TypeException("not implemented");
        }
    }

    private static class MetaEncodingReader implements TypeReader {
        private TypeReader _data;
        private String _encoding;

        private MetaEncodingReader(TypeReader expression, String encoding) {
            _data = expression;
            _encoding = encoding;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            byte[] data = (byte[]) _data.read(in);
            return new String(data, _encoding);
        }

    }
}
