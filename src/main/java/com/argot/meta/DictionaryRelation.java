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
import com.argot.TypeLocation;
import com.argot.TypeLocationRelation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeBeanMarshaller;

public class DictionaryRelation extends DictionaryLocation implements TypeLocationRelation {
    public static final String TYPENAME = "dictionary.relation";

    private int _id;
    private String _tag;

    public DictionaryRelation() {
        super(TypeLocation.RELATION);
    }

    public DictionaryRelation(int targetId, String tag) {
        super(TypeLocation.RELATION);
        _id = targetId;
        _tag = tag;
    }

    @Override
    public int getId() {
        return _id;
    }

    public void setId(int id) {
        _id = id;
    }

    @Override
    public String getTag() {
        return _tag;
    }

    public void setTag(String tag) {
        _tag = tag;
    }

    public static class DictionaryRelationTypeReader implements TypeReader, TypeBound, TypeLibraryReader {
        TypeBeanMarshaller _reader = new TypeBeanMarshaller();

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
            DictionaryRelation rel = (DictionaryRelation) reader.read(in);

            // Check that what its referencing exists and convert from
            // external mapping to internal mapping.
            TypeMap refMap = (TypeMap) in.getTypeMap().getReference(TypeMap.REFERENCE_MAP);

            if (refMap.isValid(rel.getId()))
                rel.setId(refMap.getDefinitionId(rel.getId()));
            else
                throw new TypeException("DictionaryDefinition: invalid id " + rel.getId());

            return rel;
        }
    }

    public static class DicitonaryRelationTypeWriter implements TypeWriter, TypeBound, TypeLibraryWriter {
        TypeBeanMarshaller _writer = new TypeBeanMarshaller();

        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            DictionaryRelation dd = (DictionaryRelation) o;
            TypeMap refMap = (TypeMap) out.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
            int id = refMap.getStreamId(dd.getId());
            DictionaryRelation dr = new DictionaryRelation(id, dd._tag);
            _writer.getWriter(out.getTypeMap()).write(out, dr);
        }

        @Override
        public TypeWriter getWriter(TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public void bind(TypeLibrary library, int definitionId, TypeElement definition) throws TypeException {
            _writer.bind(library, definitionId, definition);
        }
    }
}
