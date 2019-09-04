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
package com.argot.auto;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeConstructor;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;

public class TypeReaderAuto implements TypeBound, TypeLibraryReader {
    private MetaExpressionResolver _resolver;
    private TypeConstructor _constructor;
    private MetaSequence _metaSequence;

    public TypeReaderAuto(Class<?> clss) {
        _resolver = new MetaExpressionLibraryResolver();
        _constructor = new TypeConstructorAuto(clss);
        _metaSequence = null;
    }

    public TypeReaderAuto(TypeConstructor constructor) {
        _resolver = new MetaExpressionLibraryResolver();
        _constructor = constructor;
    }

    @Override
    public void bind(TypeLibrary library, int definitionId, TypeElement definition) throws TypeException {
        if (!(definition instanceof MetaSequence)) {
            if (!(definition instanceof MetaSequence)) {
                throw new TypeException("TypeReaderAuto: required MetaSequence to read data.");
            }

            _metaSequence = (MetaSequence) definition;
            return;
        }
        _metaSequence = (MetaSequence) definition;
    }

    private class TypeAutoReader implements TypeReader {
        private TypeReader _sequence;
        private MetaSequence _metaSequence;

        public TypeAutoReader(TypeReader sequence, MetaSequence metaSequence) {
            _sequence = sequence;
            _metaSequence = metaSequence;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            Object[] objects = (Object[]) _sequence.read(in);
            return _constructor.construct(_metaSequence, objects);
        }
    }

    @Override
    public TypeReader getReader(TypeMap map) throws TypeException {
        return new TypeAutoReader(_resolver.getExpressionReader(map, _metaSequence), _metaSequence);
    }

}
