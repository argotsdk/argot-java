/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaArray
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME = "meta.array";
	public static final String VERSION = "1.3";
	
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
    
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException
    {
        super.bind(library, definitionId, location, definition);
        _size.bind( library, definitionId, null, definition );
        _type.bind( library, definitionId, null, definition );
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
	    return library.getDefinitionId( TYPENAME, VERSION );
	}

	public static class MetaArrayTypeReader
	extends MetaExpressionReaderAuto
	implements MetaExpressionReader
	{
		public MetaArrayTypeReader() 
		{
			super(MetaArray.class);
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaArray metaArray = (MetaArray) element;
			return new MetaArrayReader( resolver.getExpressionReader(map, metaArray._size), resolver.getExpressionReader(map, metaArray._type));
		}	
	}    
	
	public static class MetaArrayTypeWriter
	implements TypeLibraryWriter,TypeWriter,MetaExpressionWriter
	{
		public void write(TypeOutputStream out, Object o )
		throws TypeException, IOException
		{
			MetaArray ma = (MetaArray) o;
			out.writeObject( MetaExpression.TYPENAME, ma._size );
			out.writeObject( MetaExpression.TYPENAME, ma._type );
					
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			throw new TypeException("not implemented");
		}		
	}
	
	private static class MetaArrayReader
	implements TypeReader
	{
		TypeReader _size;
		TypeReader _data;
		
		private MetaArrayReader(TypeReader size, TypeReader data)
		{
			_size = size;
			_data = data;
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
		    Object sizeObject =  _size.read(in);
		    
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
				objects[x] = _data.read( in );
			}
			
			return objects;
		}
		
	}
}
