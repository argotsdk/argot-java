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
import com.argot.common.U8Utf8;

public class MetaEncoding
extends MetaExpression
implements MetaDefinition
{
    public static final String TYPENAME = "meta.encoding";
	public static final String VERSION = "1.3";
    
	private MetaExpression _expression;
	private String _encoding;
	
	public MetaEncoding( MetaExpression expression, String encoding )
	{
		_expression = expression;
		_encoding = encoding;	
	}

    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) throws TypeException
    {
        super.bind(library, definitionId, location, definition);
        _expression.bind( library, definitionId, null, definition );
    }	

	public static class MetaEncodingTypeReader
	extends MetaExpressionReaderAuto
	implements MetaExpressionReader
	{
		public MetaEncodingTypeReader() 
		{
			super(MetaEncoding.class);
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaEncoding metaEncoding = (MetaEncoding) element;
	    	return new MetaEncodingReader(resolver.getExpressionReader(map, metaEncoding._expression), metaEncoding._encoding);			
		}	
	}    
    
    public static class MetaEncodingTypeWriter
    implements TypeLibraryWriter,TypeWriter,MetaExpressionWriter
    {
	    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
	    {
	    	MetaEncoding enc = (MetaEncoding) o;
	
	 		out.writeObject( MetaExpression.TYPENAME, enc._expression );
			out.writeObject( U8Utf8.TYPENAME, enc._encoding );	
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

    private static class MetaEncodingReader
    implements TypeReader
    {
    	private TypeReader _data;
    	private String _encoding;
    	
    	private MetaEncodingReader( TypeReader expression, String encoding )
    	{
    		_data = expression;
    		_encoding = encoding;
    	}
    	
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
	        byte[] data = (byte[]) _data.read( in );
	        return new String( data, _encoding );
		}
    	
    }
}
