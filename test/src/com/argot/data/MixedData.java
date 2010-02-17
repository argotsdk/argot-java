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

package com.argot.data;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;


/*
 * A simple mixed data used for testing purposes.
 * 
 * mixeddata: meta.sequence([
 * 		meta.tag( "int32", meta.reference( #s32 ));
 * 		meta.tag( "int16", meta.reference( #s16 ));
 * 		meta.tag( "u8ascii", meta.reference( #u8ascii ));
 * 
 */
public class MixedData 
{
	public static final String TYPENAME = "mixeddata";
	public static final String VERSION = "1.0";
	
	private int _anInt;
	private short _aShort;
	private String _anAscii;
	
	public MixedData( int anInt, short aShort, String anAscii )
	{
		_anInt = anInt;
		_aShort = aShort;
		_anAscii = anAscii;
	}
	
	public int getInt()
	{
		return _anInt;
	}
	
	public short getShort()
	{
		return _aShort;
	}
	
	public String getString()
	{
		return _anAscii;
	}
	
	public static class MixedDataWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MixedData data = (MixedData) o;
			out.writeObject("uint16", new Integer( data._anInt ));
			out.writeObject("uint8", new Short( data._aShort ));
			out.writeObject("u8ascii", data._anAscii );
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}		
	}

	/*
	 * This should be contained in a dictionary file instead
	 * of being created in code.  Useful here for testing.
	 */
	public static int register( TypeLibrary library )
	throws TypeException
	{
		int id = library.register( new DictionaryName(MetaName.parseName(library,TYPENAME)), new MetaIdentity() );
		
		return library.register( 
				new DictionaryDefinition(id, MetaName.parseName(library,TYPENAME), MetaVersion.parseVersion("1.0")),
				new MetaSequence(
					new MetaExpression[]{
					    new MetaTag( "uint16", new MetaReference( library.getTypeId("uint16"))),
					    new MetaTag( "uint8", new MetaReference( library.getTypeId("uint8"))),
					    new MetaTag( "u8ascii", new MetaReference( library.getTypeId("u8ascii")))
					}
				),
			new TypeReaderAuto( MixedData.class ),
			new MixedDataWriter(),
			MixedData.class
		);
	}
}
