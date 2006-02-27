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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;

import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;

/*
 * An meta.envelop when called will take any object and attempt to write it out to a
 * temporary buffer.  The buffer will then be written with a size.  Size should be
 * an expression as a reference to a size like u16.  Expressions have been allowed
 * because it can be specified that way.
 */
public class MetaEnvelop
extends MetaBase
implements MetaExpression, MetaDefinition
{
	public static final String TYPENAME = "meta.envelop";
	
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
    
    public static class MetaEnvelopTypeReader
    implements TypeReader
    {
		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
		{
			if ( element instanceof MetaExpression )
			{
				TypeReader reader = new TypeReaderAuto( this.getClass() );
				return reader.read( in, element );
			}
			throw new TypeException( "shouldn't get here.");		
		}
    }

    public static class MetaEnvelopTypeWriter
    implements TypeWriter
    {
		public void write(TypeOutputStream out, Object o, TypeElement element )
		throws TypeException, IOException
		{
			MetaEnvelop ma = (MetaEnvelop) o;
	
			out.writeObject( "meta.expression", ma._size );
			out.writeObject( "meta.expression", ma._type );
					
		}
    }

	public Object doRead(TypeInputStream in)
	throws TypeException, IOException 
	{
	    Object sizeObject = _size.doRead( in );
	    
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
		in.getStream().read( buffer );
		return buffer;
	}


	public void doWrite(TypeOutputStream out, Object o)
	throws TypeException, IOException 
	{
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		
		TypeOutputStream tmos = new TypeOutputStream( bout, out.getTypeMap() );
		
		_type.doWrite( tmos, o );
		bout.close();
		
		byte b[] = bout.toByteArray();
		
		_size.doWrite( out, new Integer(b.length) );
		out.getStream().write( b );	
		
	}

}
