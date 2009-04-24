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

import java.io.ByteArrayOutputStream;
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

/*
 * An meta.envelop when called will take any object and attempt to write it out to a
 * temporary buffer.  The buffer will then be written with a size.  Size should be
 * an expression as a reference to a size like u16.  Expressions have been allowed
 * because it can be specified that way.
 */
public class MetaEnvelop
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME = "meta.envelop";
	public static final String VERSION = "1.3";
	
	private MetaExpression _size;
	private MetaExpression _type;
	
	public MetaEnvelop( MetaExpression size, MetaExpression type )
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
        _size.bind(library, definitionId, null, definition);
        _type.bind(library, definitionId, null, definition);
    }
    
    public MetaExpression getSizeExpression()
    {
    	return _size;
    }
    
    public MetaExpression getTypeExpression()
    {
    	return _type;
    }
    
	public static class MetaEnvelopTypeReader
	extends MetaExpressionReaderAuto
	implements MetaExpressionReader
	{
		public MetaEnvelopTypeReader() 
		{
			super(MetaEnvelop.class);
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaEnvelop metaEnvelop = (MetaEnvelop) element;
			return new MetaEnvelopReader(resolver.getExpressionReader(map, metaEnvelop._size));		
		}	
	}    
    
    public static class MetaEnvelopTypeWriter
    implements TypeLibraryWriter,TypeWriter,MetaExpressionWriter
    {
		public void write(TypeOutputStream out, Object o )
		throws TypeException, IOException
		{
			MetaEnvelop ma = (MetaEnvelop) o;
	
			out.writeObject( "meta.expression", ma._size );
			out.writeObject( "meta.expression", ma._type );					
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaEnvelop metaEnvelop = (MetaEnvelop) element;
			return new MetaEnvelopWriter( resolver.getExpressionWriter(map, metaEnvelop._type), resolver.getExpressionWriter(map, metaEnvelop._size));
		}
    }

    private static class MetaEnvelopReader
    implements TypeReader
    {
    	private TypeReader _size;
    	
    	private MetaEnvelopReader( TypeReader size )
    	{
    		_size = size;
    	}
    	
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
		    Object sizeObject = _size.read( in );
		    
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
			else
			{
				throw new TypeException("meta.envelop not able to use size object");
			}
			
			byte[] buffer = new byte[size];
			in.read( buffer,0,buffer.length );
			return buffer;
		}
    	
    }
 
	private static class MetaEnvelopWriter
	implements TypeWriter
	{
		private TypeWriter _type;
		private TypeWriter _size;
		
		public MetaEnvelopWriter(TypeWriter type, TypeWriter size )
		{
			_type = type;
			_size = size;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{		
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			TypeOutputStream tmos = new TypeOutputStream( bout, out.getTypeMap() );
			
			_type.write( tmos, o );
			bout.close();
			
			byte b[] = bout.toByteArray();

			_size.write( out, new Integer(b.length) );
			out.getStream().write( b );	
		}
		
	}
}
