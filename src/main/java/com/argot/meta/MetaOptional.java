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
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaOptional extends MetaExpression {
    public static final String TYPENAME = "meta.optional";
    public static final String VERSION = "1.3";

    private MetaExpression _option;

    public MetaOptional(MetaExpression option) {
        _option = option;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    public MetaExpression getOptionalExpression() {
        return _option;
    }

    public static class MetaOptionalTypeReader extends MetaExpressionReaderAuto implements MetaExpressionReader {
        public MetaOptionalTypeReader() {
            super(MetaOptional.class);
        }

        @Override
        public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaOptional metaOptional = (MetaOptional) element;
            return new MetaOptionalReader(map.getReader(map.getStreamId("bool")), resolver.getExpressionReader(map, metaOptional._option));
        }
    }

    public static class MetaOptionalTypeWriter implements TypeLibraryWriter, TypeWriter, MetaExpressionWriter {
        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            MetaOptional to = (MetaOptional) o;

            out.writeObject("meta.expression", to._option);
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaOptional metaOptional = (MetaOptional) element;
            return new MetaOptionalWriter(map.getWriter(map.getStreamId("bool")), resolver.getExpressionWriter(map, metaOptional._option));
        }
    }

    private static class MetaOptionalReader implements TypeReader {
        private TypeReader _bool;
        private TypeReader _option;

        public MetaOptionalReader(TypeReader bool, TypeReader option) {
            _bool = bool;
            _option = option;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            Boolean id = (Boolean) _bool.read(in);
            if (id.booleanValue())
                return _option.read(in);
            return null;
        }

    }

    private static class MetaOptionalWriter implements TypeWriter {
        private TypeWriter _bool;
        private TypeWriter _option;

        public MetaOptionalWriter(TypeWriter bool, TypeWriter option) {
            _bool = bool;
            _option = option;
        }

        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            if (o == null) {
                _bool.write(out, Boolean.FALSE);
                return;
            }

            _bool.write(out, Boolean.TRUE);
            _option.write(out, o);
        }

    }
}
