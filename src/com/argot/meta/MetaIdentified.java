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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;

/**
 * An Identified object uses a Short to write out the type of the object
 * to be written.  This allows for things like an array of known objects
 * to be written.
 */
public class MetaIdentified
extends MetaBase
implements MetaExpression
{
	public static String TYPENAME = "meta.identified";

	private String _description;

	public MetaIdentified( String description )
	{
		_description = description;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public static class MetaIdentifiedTypeReader
    implements TypeReader
    {
		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
		{
			TypeReader reader = new TypeReaderAuto( MetaIdentified.class );
			return reader.read( in, element );
		}
    }

    public static class MetaIdentifiedTypeWriter
    implements TypeWriter
    {
		public void write(TypeOutputStream out, Object o, TypeElement element )
			throws TypeException, IOException
		{
			MetaIdentified ti = (MetaIdentified) o;
			
			out.writeObject( "meta.name", ti._description );
		}
    }
    
	public Object doRead(TypeInputStream in )
	throws TypeException, IOException 
	{
		Integer id = (Integer) in.readObject( "u16" );
		return in.readObject( id.intValue() );  
	}

	public void doWrite(TypeOutputStream out, Object o)
	throws TypeException, IOException 
	{
		int systemId = out.getTypeMap().getLibrary().getId(o.getClass());
		int id = out.getTypeMap().getId(systemId);
		out.writeObject( "u16", new Integer(id));
		out.writeObject( id, o );
	}

}
