/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
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
package com.argot.dictionary;

import java.io.IOException;
import java.util.Iterator;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.UInt16;
import com.argot.common.UVInt28;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.MetaDefinition;

public class TypeMapMarshaller
implements TypeLibraryWriter, TypeWriter
{
    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
    {
		TypeMap map = (TypeMap) o;
		TypeLibrary library = map.getLibrary();
		
		// write the length out first.
		out.writeObject( UVInt28.TYPENAME, new Integer( map.size() ));
				
		Iterator i = map.getIdList().iterator();
		while (i.hasNext() )
		{
			int streamId = ((Integer) i.next()).intValue();
			int definitionId = map.getDefinitionId(streamId);

			out.writeObject( UVInt28.TYPENAME, new Integer(streamId));
			out.writeObject( DictionaryLocation.TYPENAME, library.getLocation(definitionId));
			out.writeObject( MetaDefinition.META_DEFINITION_ENVELOPE, library.getStructure( definitionId ) );			
		}

    }
    
	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return this;
	}
    

}
