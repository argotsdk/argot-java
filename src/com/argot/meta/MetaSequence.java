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
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaSequence
extends MetaBase
implements MetaExpression, MetaDefinition
{
	public static String TYPENAME  = "meta.sequence";
	
	private MetaExpression[] _objects;
	
	public MetaSequence( Object[] objects )
	{
	    _objects = new MetaExpression[ objects.length ];
	    
	    for ( int x=0; x< objects.length; x++)
	    {
    		_objects[x] = (MetaExpression) objects[x];
	    }
	}
	
	public MetaSequence( MetaExpression[] objects )
	{
		_objects = objects;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) throws TypeException
    {
        super.bind(library, definition, typeName, typeId);
        for ( int x=0; x< _objects.length; x++ )
        {
            _objects[x].bind( library, definition, typeName, typeId );
        }     
    }
	public MetaExpression getElement( int x )
	{
		return (MetaExpression) _objects[x];
	}
	
	public int size()
	{
		return _objects.length;
	}
	
	public static class MetaSequenceTypeWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object obj ) 
		throws TypeException, IOException
		{
			MetaSequence ts = (MetaSequence) obj;
	
			out.writeObject(  "u8", new Integer( ts._objects.length ));
	
			for ( int x=0 ; x < ts._objects.length ; x++ )
			{
				Object o  = ts._objects[x];
				out.writeObject( "meta.expression", o );
			}
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
		
	}
	
	private class MetaSequenceReader
	implements TypeReader
	{
		private TypeReader[] _readers;
		
		public MetaSequenceReader( TypeReader[] readers )
		{
			_readers = readers;
		}

		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			Object[] objects = new Object[ size() ];
			
			for ( int x=0; x < size() ; x++ )
			{
				
				objects[x] = _readers[x].read(in);
			}			
			return objects;
		}
		
	}
	
	public TypeReader getReader(TypeMap map)
	throws TypeException 
	{
		TypeReader[] readers = new TypeReader[ size() ];
		
		for ( int x=0; x < size() ; x++ )
		{
			readers[x] = getElement(x).getReader(map);
		}		
		return new MetaSequenceReader(readers);
	}

	public TypeWriter getWriter(TypeMap map)
	throws TypeException 
	{
		throw new TypeException("not implemented");
	}
}