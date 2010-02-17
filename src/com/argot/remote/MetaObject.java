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
package com.argot.remote;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;


/**
 * This is an abstract type.  Each system will implement its
 * own type of object location description.
 * 
 */

public class MetaObject 
{
	public static final String TYPENAME = "remote.object";
	public static final String VERSION = "1.3";

	private MetaLocation _location;
	private int _type;
	
	public MetaObject( MetaLocation location, int type )
	{
		_location = location;
		_type = type;
	}
	
	public MetaLocation getLocation()
	{
		return _location;
	}
	
	public int getType()
	{
		return _type;
	}
	
	public static class MetaObjectReader
	implements TypeReader
	{
		public Object read(TypeInputStream in) 
		throws TypeException, IOException 
		{
			MetaLocation location = (MetaLocation) in.readObject(MetaLocation.TYPENAME);
			Integer id = (Integer) in.readObject(UInt16.TYPENAME );
			int sysId = in.getTypeMap().getDefinitionId( id.intValue() );
			return new MetaObject( location, sysId );
		}
	}

	public static class MetaObjectWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MetaObject obj = (MetaObject) o;
			
			out.writeObject( MetaLocation.TYPENAME, obj.getLocation() );
			int mapId = out.getTypeMap().getStreamId( obj.getType() );
			out.writeObject(  UInt16.TYPENAME, new Integer( mapId ) );
		}
	}
}
