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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaExpressionLibraryResolver implements MetaExpressionResolver {

    @Override
    public TypeReader getExpressionReader(TypeMap map, MetaExpression expression) throws TypeException {
        // Force the streamId to be mapped using a getStreamId.
        map.getStreamId(expression.getMemberTypeId());

        // Get the typeId of the expression and use it to get the reader.
        TypeLibraryReader reader = map.getLibrary().getReader(getMetaDefinitionId(map, expression.getTypeId()));
        if (!(reader instanceof MetaExpressionReader)) {
            throw new TypeException("MetaExpressionReader expected. Found: " + reader.getClass().getName());
        }
        MetaExpressionReader expressionReader = (MetaExpressionReader) reader;
        return expressionReader.getExpressionReader(map, this, expression);
    }

    @Override
    public TypeWriter getExpressionWriter(TypeMap map, MetaExpression expression) throws TypeException {
        // Force the streamId to be mapped using the following.
        map.getStreamId(expression.getMemberTypeId());

        TypeLibraryWriter writer = map.getLibrary().getWriter(getMetaDefinitionId(map, expression.getTypeId()));
        if (!(writer instanceof MetaExpressionWriter)) {
            throw new TypeException("MetaExpressionLibraryResolver: MetaExpressionWriter expected. Found: " + writer.getClass().getName());
        }
        MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
        return expressionWriter.getExpressionWriter(map, this, expression);
    }

    /*
     * This finds the metaIdentity of the type that was used to define the type.
     * It then gets the definition of the type and returns the typeId.
     * Required because it shouldn't map the definition id.
     */
    private int getMetaDefinitionId(TypeMap map, int metaTypeId) throws TypeException {
        TypeElement typeStructure = map.getLibrary().getStructure(metaTypeId);
        if (!(typeStructure instanceof MetaIdentity)) {
            // This shouldn't be possible.
            throw new TypeException("MetaExpressionLibraryResolver: Failed to find MetaIdentity for type");
        }

        // There must only be one version of a Meta type.  Get it.
        MetaIdentity identity = (MetaIdentity) typeStructure;
        Integer[] versions = identity.getVersionIdentifiers();
        if (versions.length != 1) {
            throw new TypeException("MetaExpressionLibraryResolver: More than one version defined for MetaExpression type");
        }

        return versions[0];

    }
}
