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

package com.argot.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.common.CommonLoader;
import com.argot.data.MixedData;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class MessageTest 
extends TestCase
{
	private TypeLibrary _library;
	private int _mixedDataTypeId;
	
	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new CommonLoader()
	};
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary( libraryLoaders );
        _mixedDataTypeId = MixedData.register( _library );
    }
    
    public void testTypeMapCore() throws Exception
    {
        MixedData data = new MixedData( 2345, (short)234, "Testing");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageWriter writer = new MessageWriter( _library );
        writer.writeMessage( baos, _mixedDataTypeId, data );
        baos.close();
        
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        Object o = MessageReader.readMessage( _library, bais );
        assertEquals( o.getClass(), data.getClass() );
        MixedData readData = (MixedData) data;
        assertEquals( readData.getInt(), data.getInt() );
        assertEquals( readData.getShort(), data.getShort() );
        assertEquals( readData.getString(), data.getString() );
        
     }
}
