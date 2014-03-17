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
		if ( size == null ) throw new IllegalArgumentException("MetaArray: size is null");
		if ( type == null ) throw new IllegalArgumentException("MetaArray: type is null");

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
			MetaArray metaArray = (MetaArray) element;
			return new MetaArrayWriter( resolver.getExpressionWriter(map, metaArray._size), resolver.getExpressionWriter(map, metaArray._type));
		}		
	}
	
	private static class MetaArrayWriter
	implements TypeWriter
	{
		TypeWriter _size;
		TypeWriter _data;
		
		private MetaArrayWriter(TypeWriter size, TypeWriter data )
		{
			_size = size;
			_data = data;
		}
		
		@Override
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			if (!o.getClass().isArray())
				throw new TypeException("Attempting to write non-array type");
			
			Long[] array = (Long[])o;
			
			_size.write(out, array.length );
			
			for ( int x = 0 ; x < array.length; x ++ )
			{
				_data.write( out, array[x] );
			}
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
