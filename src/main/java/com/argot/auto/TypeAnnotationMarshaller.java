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
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReaderWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

/**
 * TypeAnnotationMarshaller uses the ArgotTag annotation to map Fields to named elements in a sequence.
 */
public class TypeAnnotationMarshaller implements TypeLibraryReaderWriter, TypeBound {
    MetaExpressionResolver _expressionResolver;
    MetaSequence _sequence;
    Class<?> _typeClass;
    Field[] _fields;

    public TypeAnnotationMarshaller() {
        this(new MetaExpressionLibraryResolver());
    }

    public TypeAnnotationMarshaller(MetaExpressionResolver resolver) {
        _expressionResolver = resolver;
    }

    @Override
    public void bind(TypeLibrary library, int definitionId, TypeElement definition) throws TypeException {
        if (!(definition instanceof MetaSequence)) {
            throw new TypeException("TypeAnnotationMarshaller: Not an array instance");
        }

        _sequence = (MetaSequence) definition;
        _typeClass = library.getClass(definitionId);

        _fields = new Field[_sequence.size()];
        for (int x = 0; x < _sequence.size(); x++) {
            MetaExpression expression = _sequence.getElement(x);
            if (!(expression instanceof MetaTag)) {
                throw new TypeException("TypeAnnotationMarshaller: All sequence elements must be meta.tag type.");
            }

            MetaTag metaTag = (MetaTag) expression;
            String description = metaTag.getDescription();

            Field[] classFields = _typeClass.getDeclaredFields();
            for (Field field : classFields) {
                ArgotTag tag = field.getAnnotation(ArgotTag.class);
                if (tag == null) {
                    continue;
                }

                if (tag.value().equals(description)) {
                    _fields[x] = field;
                    break;
                }
            }

            if (_fields[x] == null) {
                throw new TypeException("TypeAnnotationMarshaller: Tag '" + description + "' unmatched in Class '" + _typeClass.getName() + "'");
            }
        }

    }

    @Override
    public TypeReader getReader(TypeMap map) throws TypeException {
        TypeReader readers[] = new TypeReader[_sequence.size()];

        for (int x = 0; x < readers.length; x++) {
            readers[x] = _expressionResolver.getExpressionReader(map, _sequence.getElement(x));
        }

        return new AnnotationMarshallerReader(readers);
    }

    private class AnnotationMarshallerReader implements TypeReader {
        private TypeReader[] _sequenceReaders;

        public AnnotationMarshallerReader(TypeReader[] readers) {
            _sequenceReaders = readers;
        }

        @Override
        public Object read(TypeInputStream in) throws TypeException, IOException {
            Object o;
            try {
                o = _typeClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
                throw new TypeException("Error: No empty constructor for class ", e);
            }

            Object value;
            for (int x = 0; x < _sequenceReaders.length; x++) {
                value = _sequenceReaders[x].read(in);

                try {
                    _fields[x].set(o, value);
                } catch (IllegalArgumentException e) {
                    throw new TypeException("TypeBeanMarshaller: Failed to set for field:" + _typeClass.getName() + "." + _fields[x].getName() + " with " + value.getClass().getName(), e);
                } catch (IllegalAccessException e) {
                    throw new TypeException("TypeBeanMarshaller: Failed to set for field:" + _typeClass.getName() + "." + _fields[x].getName() + " with " + value.getClass().getName(), e);
                }

            }
            return o;
        }
    }

    @Override
    public TypeWriter getWriter(TypeMap map) throws TypeException {
        TypeWriter writers[] = new TypeWriter[_sequence.size()];

        for (int x = 0; x < writers.length; x++) {
            writers[x] = _expressionResolver.getExpressionWriter(map, _sequence.getElement(x));
        }

        return new AnnotationMarshallerWriter(writers);
    }

    private class AnnotationMarshallerWriter implements TypeWriter {
        TypeWriter[] _sequenceWriters;

        public AnnotationMarshallerWriter(TypeWriter[] sequenceWriters) {
            _sequenceWriters = sequenceWriters;
        }

        @Override
        public void write(TypeOutputStream out, Object o) throws TypeException, IOException {
            for (int x = 0; x < _sequenceWriters.length; x++) {
                try {
                    _sequenceWriters[x].write(out, _fields[x].get(o));
                } catch (IllegalArgumentException e) {
                    throw new TypeException("TypeAnnotationMarshaller: Failed to get data from field:" + _typeClass.getName() + "." + _fields[x].getName(), e);
                } catch (IllegalAccessException e) {
                    throw new TypeException("TypeAnnotationMarshaller: Failed to get data from field:" + _typeClass.getName() + "." + _fields[x].getName(), e);
                }
            }

        }
    }

}
