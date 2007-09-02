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

public class MetaArray
extends MetaBase
implements MetaExpression
{
	public static final String TYPENAME = "meta.array";
	
	private MetaExpression _size;
	private MetaExpression _type;
	
	public MetaArray( MetaExpression size, MetaExpression type )
	{
		_size = size;
		_type = type;
	}

    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) throws TypeException
    {
        super.bind(library, definition, typeName, typeId);
        _size.bind( library, definition, typeName, typeId );
        _type.bind( library, definition, typeName, typeId );
    }	
	
	public MetaExpression getSizeExpression()
	{
	    return _size;
	}
	
	public MetaExpression getTypeExpression()
	{
	    return _type;
	}
	
	public int getExpressionType( TypeLibrary library ) throws TypeException
	{
	    return library.getId( TYPENAME );
	}
	
	public static class MetaArrayTypeReader
	implements TypeReader
	{
		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
		{
			TypeReaderAuto r = new TypeReaderAuto( MetaArray.class );
			return r.read( in, element );
		}
	}

	public static class MetaArrayTypeWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o, TypeElement element )
		throws TypeException, IOException
		{
			MetaArray ma = (MetaArray) o;
			out.writeObject( "meta.expression", ma._size );
			out.writeObject( "meta.expression", ma._type );
					
		}
	}
	
	public Object doRead(TypeInputStream in)
	throws TypeException, IOException 
	{
	    Object sizeObject =  _size.doRead( in );
	    
		int size = 0;
		
		if ( sizeObject instanceof Byte )
		{
			size = ((Byte)sizeObject).intValue();
		}
		else if (sizeObject instanceof Short)
		{
			size = ((Short)sizeObject).intValue();
			
		}
		else if (sizeObject instanceof Integer )
		{
			size = ((Integer)sizeObject).intValue();
		}
		else if (sizeObject instanceof Long )
		{
		    size = ((Long)sizeObject).intValue();
		}
		else
		{
			throw new TypeException("MetaArray not able to use size object");
		}
				
		Object[] objects = new Object[ size ];
		for ( int x = 0 ; x < size; x ++ )
		{
			objects[x] = _type.doRead( in );
		}
		
		return objects;
	}

	public void doWrite(TypeOutputStream out, Object o)
	throws TypeException, IOException 
	{
		throw new TypeException("not implemented");
	}

}
