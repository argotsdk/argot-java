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
package com.argot;

import com.argot.meta.MetaName;

public class TypeMapperError 
implements TypeMapper 
{
	private TypeLibrary _library;

	public TypeMapperError()
	{
	}
	
	public void initialise(TypeMap map) 
	throws TypeException 
	{
		_library = map.getLibrary();
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		String name = Integer.toString(definitionId);
		try
		{
			name = _library.getName(definitionId).getFullName();
		}
		catch ( TypeException ex )
		{
			name += " - " + ex.getMessage();
		}
	
		throw new TypeException("not mapped: " + name );
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		throw new TypeException("not mapped");
	}

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		MetaName name = _library.getName(nameId);
		throw new TypeException("not mapped: " + name.getFullName() );
	}

}
