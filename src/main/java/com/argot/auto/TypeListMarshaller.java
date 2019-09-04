/*
 * Copyright (c) 2003-2015, Live Media Pty. Ltd.
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
import java.util.List;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeInstantiator;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReaderWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionLibraryResolver;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaSequence;

public class TypeListMarshaller implements TypeLibraryReaderWriter, TypeBound {
    private final MetaExpressionResolver _resolver;
    private Class<?> _typeClass;
    private TypeInstantiator _instantiator;
    private MetaExpression _sizeExpression;
    private MetaExpression _dataExpression;

    public TypeListMarshaller(final TypeInstantiator instantiator) {
        _resolver = new MetaExpressionLibraryResolver();
        _instantiator = instantiator;
    }

    public TypeListMarshaller() {
        this(null);
    }

    @Override
    public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition) throws TypeException {
        if (!(definition instanceof MetaSequence)) {
            throw new TypeException("TypeListMarshaller: array not surrounded by sequence.");
        }

        final MetaSequence sequence = (MetaSequence) definition;

        final MetaExpression expression = sequence.getElement(0);
        if (!(expression instanceof MetaArray)) {
            throw new TypeException("TypeListMarshaller: not an array instance");
        }

        final MetaArray array = (MetaArray) expression;
        _sizeExpression = array.getSizeExpression();
        _dataExpression = array.getTypeExpression();
        final Class<?> arrayClass = library.getClass(definitionId);
        if (!arrayClass.isArray()) {
            throw new TypeException("TypeListMarshaller: not bound to array data type");
        }
        _typeClass = arrayClass.getComponentType();

        if (_instantiator == null) {
            _instantiator = library.getInstantiator(definitionId);
        }

    }

    private class TypeArrayMarshallerReader implements TypeReader {
        private final Class<?> _typeClass;
        private final TypeReader _size;
        private final TypeReader _data;

        public TypeArrayMarshallerReader(final Class<?> typeClass, final TypeReader size, final TypeReader data) {
            _typeClass = typeClass;
            _size = size;
            _data = data;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Object read(final TypeInputStream in) throws TypeException, IOException {
            final Object size = _size.read(in);
            int s = 0;
            if (size instanceof Byte) {
                s = ((Byte) size).intValue();
            } else if (size instanceof Short) {
                s = ((Short) size).intValue();
            } else if (size instanceof Integer) {
                s = ((Integer) size).intValue();
            } else if (size instanceof Long) {
                s = ((Long) size).intValue();
            } else {
                if (size != null) {
                    throw new TypeException("TypeListMarshaller: Size type not an integer type: " + size.getClass().getName());
                }
                throw new TypeException("TypeListMarshaller: Size returned null value");
            }

            @SuppressWarnings("rawtypes")
            final List array = (List) _instantiator.newInstance();
            for (int x = 0; x < s; x++) {
                final Object v = _data.read(in);
                try {
                    array.set(x, v);
                } catch (final IllegalArgumentException ex) {
                    throw new TypeException("Failed to set array with Object:" + (v == null ? "null" : v.getClass().getName()) + " to " + _typeClass.getName(), ex);
                }
            }
            return array;
        }
    }

    @Override
    public TypeReader getReader(final TypeMap map) throws TypeException {
        return new TypeArrayMarshallerReader(_typeClass, _resolver.getExpressionReader(map, _sizeExpression), _resolver.getExpressionReader(map, _dataExpression));
    }

    private class TypeArrayMarshallerWriter implements TypeWriter {
        private final TypeWriter _size;
        private final TypeWriter _data;

        public TypeArrayMarshallerWriter(final TypeWriter size, final TypeWriter data) {
            _size = size;
            _data = data;
        }

        @Override
        public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
            if (o == null) {
                throw new TypeException("TypeListMarshaller: Object value is null");
            }

            if (!(o instanceof List)) {
                throw new TypeException("TypeListMarshaller: Object value is not an array type. " + o.getClass().getName());
            }

            final List<?> list = (List<?>) o;
            final int length = list.size();
            _size.write(out, Integer.valueOf(length));

            for (int x = 0; x < length; x++) {
                _data.write(out, list.get(x));
            }
        }
    }

    @Override
    public TypeWriter getWriter(final TypeMap map) throws TypeException {
        return new TypeArrayMarshallerWriter(_resolver.getExpressionWriter(map, _sizeExpression), _resolver.getExpressionWriter(map, _dataExpression));
    }

}
