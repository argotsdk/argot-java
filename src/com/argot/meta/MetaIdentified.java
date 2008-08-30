/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
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

	//private String _description;

	public MetaIdentified( /*String description*/ )
	{
		//_description = description;
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
			
			//out.writeObject( "meta.name", ti._description );
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
		return new MetaIdentifiedReader( map.getReader(map.getId(UInt16.TYPENAME)));  
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
			int systemId = out.getTypeMap().getLibrary().getId(o.getClass());
			int id = out.getTypeMap().getId(systemId);
			_uint16.write( out, new Integer(id));
			out.writeObject( id, o );
		}
		
	}

	public TypeWriter getWriter(TypeMap map)
	throws TypeException 
	{
		return new MetaIdentifiedWriter(map.getWriter(map.getId(UInt16.TYPENAME)));
	}

}
