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
import com.argot.TypeLibrary;
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
	
	
	/**
	 * This is used to read an actual sequence from a definition which
	 * uses this function.
	 * 
	 * @see com.argot.TypeFunction#read(com.argot.TypeMimeInputStream, com.argot.TypeElement)
	 */
	public static class MetaSequenceTypeReader
	implements TypeReader
	{
		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
		{
			if ( element instanceof MetaExpression )
			{
				TypeReader reader = new TypeReaderAuto( MetaSequence.class );
				return reader.read( in, element );
			}
			throw new TypeException( "shouldn't get here.");
		}
	}
		
	public static class MetaSequenceTypeWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object obj, TypeElement element ) 
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
	}
	
	public Object doRead(TypeInputStream in)
	throws TypeException, IOException 
	{
		Object[] objects = new Object[ size() ];
		
		for ( int x=0; x < size() ; x++ )
		{
			
			MetaExpression te = getElement( x );
			objects[x] = te.doRead( in );
		}
		
		return objects;
	}

	public void doWrite(TypeOutputStream out, Object o)
	throws TypeException, IOException 
	{
		throw new TypeException("not implemented");
	}
}