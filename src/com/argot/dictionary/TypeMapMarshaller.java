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
package com.argot.dictionary;

import java.io.IOException;
import java.util.Iterator;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;

public class TypeMapMarshaller
implements TypeLibraryWriter, TypeWriter
{
    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
    {
		TypeMap map = (TypeMap) o;
		
		
		// write the length out first.
		out.writeObject( "U16", new Integer( map.size() ));
				
		Iterator i = map.getIterator();
		while (i.hasNext() )
		{
			int id = ((Integer) i.next()).intValue();	
			String name = map.getName( id );
			TypeElement elem = map.getStructure( id );

			out.writeObject( "U16", new Integer(id));
			out.writeObject( "meta.name", name );
			out.writeObject( "dictionary.definition", elem );			
		}

    }
    
	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return this;
	}
    

}
