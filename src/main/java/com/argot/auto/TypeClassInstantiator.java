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

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.function.Supplier;

import com.argot.TypeException;
import com.argot.TypeInstantiator;

public class TypeClassInstantiator implements TypeInstantiator {

    private final Supplier<?> constructor;

    public TypeClassInstantiator(final Class<?> clss) {
        this.constructor = getConstructor(clss);
    }

    private Supplier<?> getConstructor(Class<?> clss) {
        Supplier<?> result = null;
        if (clss != null) {
            try {
                result = new NewInstanceSupplier(clss.getConstructor());
            } catch (NoSuchMethodException | SecurityException e) {
                result = new NullSupplier();
            }
        }
        return result;
    }

    @Override
    public Object newInstance() throws TypeException {
        return constructor.get();
    }

    @SuppressWarnings("rawtypes")
    private static class NewInstanceSupplier implements Supplier {

        private final Constructor<?> constructor;

        public NewInstanceSupplier(Constructor<?> constructor) {
            this.constructor = constructor;
        }

        @Override
        public Object get() {
            try {
                return constructor.newInstance();
            } catch (final InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

    }

    @SuppressWarnings("rawtypes")
    private static class NullSupplier implements Supplier {

        @Override
        public Object get() {
            throw new RuntimeException("No constructor");
        }

    }

}