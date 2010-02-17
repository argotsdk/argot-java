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
package com.argot.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class EnvelopData
extends ModelData
{
	private MetaEnvelope _metaEnvelop;
	private Object _data;
	
	public EnvelopData( MetaEnvelope metaEnvelop, Object data )
	{
		_metaEnvelop = metaEnvelop;
		_data = data;
	}
	
	public Object getData() 
	{
		return _data;
	}

	public MetaExpression getStructure() 
	{
		return _metaEnvelop;
	}
	
	public static class EnvelopDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaEnvelope metaEnvelop = (MetaEnvelope) element;
			return new EnvelopDataReader(metaEnvelop, resolver.getExpressionReader(map, metaEnvelop.getSizeExpression()), resolver.getExpressionReader(map, metaEnvelop.getTypeExpression()));		
		}	
		
	}
	
    private static class EnvelopDataReader
    implements TypeReader
    {
    	private MetaEnvelope _metaEnvelop;
    	private TypeReader _size;
    	private TypeReader _type;
    	
    	private EnvelopDataReader( MetaEnvelope metaEnvelop, TypeReader size, TypeReader type)
    	{
    		_metaEnvelop = metaEnvelop;
    		_size = size;
    		_type = type;
    	}
    	
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
		    Object sizeObject = _size.read( in );
		    if (sizeObject instanceof ModelData)
		    {
		    	sizeObject = ((ModelData)sizeObject).getData();
		    }
		    if (sizeObject instanceof SimpleData)
		    {
		    	sizeObject = ((SimpleData)sizeObject).getData();
		    }
		    
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
			
			// Read in the buffer.
			byte[] buffer = new byte[size];
			in.read( buffer,0,buffer.length );
			
			// Read in the data from the buffer.
			ByteArrayInputStream bin = new ByteArrayInputStream(buffer);
			TypeInputStream tin = new TypeInputStream(bin,in.getTypeMap());
			Object o = _type.read(tin);
			
			return new EnvelopData( _metaEnvelop, o );
		}
    	
    }


	public static class EnvelopDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaEnvelope metaEnvelop = (MetaEnvelope) element;
			return new EnvelopDataWriter(getSizeWriter(map, resolver, metaEnvelop.getSizeExpression()), resolver.getExpressionWriter(map, metaEnvelop.getTypeExpression()));		
		}
		
		private TypeWriter getSizeWriter(TypeMap map,MetaExpressionResolver resolver, MetaExpression expression) 
		throws TypeException
		{
	    	TypeLibraryWriter writer = map.getLibrary().getWriter(expression.getTypeId());
			if(!(writer instanceof MetaExpressionWriter))
			{
				throw new TypeException("MetaExpressionReader expected. Found: " + writer.getClass().getName() );
			}
			MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
			return expressionWriter.getExpressionWriter(map, resolver, expression);    	
		}		
	}	

	private static class EnvelopDataWriter
	implements TypeWriter
	{
		private TypeWriter _size;
		private TypeWriter _type;
		
		public EnvelopDataWriter(TypeWriter size, TypeWriter type )
		{
			_size = size;
			_type = type;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{	
			EnvelopData data = (EnvelopData) o;
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			
			TypeOutputStream tmos = new TypeOutputStream( bout, out.getTypeMap() );
			
			_type.write( tmos, data.getData() );
			bout.close();
			
			byte b[] = bout.toByteArray();
			_size.write( out, new Integer(b.length) );
			out.getStream().write( b );	
		}
		
	}
	
}
