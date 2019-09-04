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

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UVInt28;

public class MetaReference extends MetaExpression implements MetaDefinition {
    public static final String TYPENAME = "meta.reference";
    public static final String VERSION = "1.3";

    private int _type;

    public MetaReference(int type) {
        _type = type;
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    public int getType() {
        return _type;
    }

    private void setType(int type) {
        _type = type;
    }

    public static class MetaReferenceTypeReader implements TypeReader, TypeBound, TypeLibraryReader, MetaExpressionReader {
        TypeReaderAuto _reader = new TypeReaderAuto(MetaReference.class);

        @Override
        public void bind(TypeLibrary library, int definitionId, TypeElement definition) throws TypeException {
            _reader.bind(library, definitionId, definition);
        }

        @Override
        public TypeReader getReader(TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {

            // Use the Automatic reader to read and create this object.
            TypeReader reader = _reader.getReader(in.getTypeMap());
            MetaReference ref = (MetaReference) reader.read(in);

            // Check that what its referencing exists and convert from
            // external mapping to internal mapping.
            TypeMap refMap = (TypeMap) in.getTypeMap().getReference(TypeMap.REFERENCE_MAP);

            if (refMap.isValid(ref.getType()))
                ref.setType(refMap.getNameId(ref.getType()));
            else
                throw new TypeException("TypeReference: invalid id " + ref.getType());

            return ref;
        }

        @Override
        public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaReference metaReference = (MetaReference) element;
            int refNameId = map.getStreamId(metaReference._type);
            return map.getReader(refNameId);
        }
    }

    public static class MetaReferenceTypeWriter implements TypeWriter, TypeLibraryWriter, MetaExpressionWriter {
        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            MetaReference tr = (MetaReference) o;
            TypeMap refMap = (TypeMap) out.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
            int id = refMap.getStreamId(tr._type);
            out.writeObject(UVInt28.TYPENAME, Integer.valueOf(id));
        }

        @Override
        public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element) throws TypeException {
            MetaReference metaReference = (MetaReference) element;
            // First map the name type.
            int refNameId = map.getStreamId(metaReference._type);

            // Next map the defaultId.
            return map.getWriter(refNameId);
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }
    }
}
