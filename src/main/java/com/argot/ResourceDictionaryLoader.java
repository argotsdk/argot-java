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

package com.argot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import com.argot.dictionary.Dictionary;

public class ResourceDictionaryLoader implements TypeLibraryLoader {
    private String _resource;

    public ResourceDictionaryLoader(String resource) {
        _resource = resource;
    }

    private InputStream getDictionaryStream(String location) {
        File dictionaryFile = new File(location);
        if (dictionaryFile.exists()) {
            try {
                return new FileInputStream(dictionaryFile);
            } catch (FileNotFoundException e) {
                // ignore and drop through.
            }
        }

        ClassLoader cl = this.getClass().getClassLoader();
        InputStream is = cl.getResourceAsStream(location);
        if (is == null) {
            return null;
        }
        return is;
    }

    @Override
    public void load(TypeLibrary library) throws TypeException {
        InputStream is = getDictionaryStream(_resource);
        if (is == null) {
            throw new TypeException("Failed to load:" + _resource);
        }

        try {
            Dictionary.readDictionary(library, is);
        } catch (TypeException e) {
            throw new TypeException("Error loading dictionary: " + _resource, e);
        } catch (IOException e) {
            throw new TypeException("Error loading dictionary: " + _resource, e);
        }

        bind(library);
    }

    public void bind(TypeLibrary library) throws TypeException {

    }

    @Override
    public String getName() {
        return _resource;
    }

}
