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
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;

/**
 * An Identified object uses a Short to write out the type of the object
 * to be written.  This allows for things like an array of known objects
 * to be written.
 */
public class MetaIdentified
extends MetaExpression
{
	public static String TYPENAME = "meta.identified";
    public static final String VERSION = "1.3";

	private String _description;

	public MetaIdentified( String description )
	{
		_description = description;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }

    public static class MetaIdentifiedTypeWriter
    implements TypeLibraryWriter,TypeWriter
    {
		public void write(TypeOutputStream out, Object o )
			throws TypeException, IOException
		{
			MetaIdentified ti = (MetaIdentified) o;
			
			out.writeObject( "u8utf8", ti._description );
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }
    
    private class MetaIdentifiedReader
    implements TypeReader
    {
    	private TypeReader _uint16;
    	
    	public MetaIdentifiedReader(TypeReader uint16)
    	{
    		_uint16 = uint16;
    	}
    	
    	public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
    		Integer id = (Integer) _uint16.read(in);
    		return in.readObject( id.intValue() );  
		}
    	
    }
    
	public TypeReader getReader(TypeMap map )
	throws TypeException 
	{
		return new MetaIdentifiedReader( map.getReader(map.getStreamId(UInt16.TYPENAME)));  
	}
	
	private class MetaIdentifiedWriter
	implements TypeWriter
	{
		private TypeWriter _uint16;
		
		public MetaIdentifiedWriter(TypeWriter uint16)
		{
			_uint16 = uint16;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if (o==null)
			{
				throw new TypeException("Identified type is null");
			}
			int streamIds[] = out.getTypeMap().getStreamId(o.getClass());
			if (streamIds.length != 1)
			{
				if (streamIds.length>1)
					throw new TypeException("Class bound to multiple system types:" +o.getClass().getName());
				if (streamIds.length==0)
					throw new TypeException("Class not bound to any mapped type:"+o.getClass().getName());
			}
			//int id = out.getTypeMap().getStreamId(systemIds[0]);
			int id = streamIds[0];
			_uint16.write( out, new Integer(id));
			out.writeObject( id, o );
		}
		
	}

	public TypeWriter getWriter(TypeMap map)
	throws TypeException 
	{
		return new MetaIdentifiedWriter(map.getWriter(map.getStreamId(UInt16.TYPENAME)));
	}

}
