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

public class MetaEncoding
extends MetaBase
implements TypeReader, TypeWriter, MetaExpression
{
    public static final String TYPENAME = "meta.encoding";
    
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
    
    public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) throws TypeException
    {
        super.bind(library, definition, typeName, typeId);
        _expression.bind( library, definition, typeName, typeId );
    }	
    
    public Object read(TypeInputStream in, TypeElement element) throws TypeException, IOException
    {
		if ( element instanceof MetaExpression )
		{
			TypeReader reader = new TypeReaderAuto( this.getClass() );
			return reader.read( in, element );
		}
		throw new TypeException( "shouldn't get here.");		
    }

    public void write(TypeOutputStream out, Object o, TypeElement element) throws TypeException, IOException
    {
    	MetaEncoding enc = (MetaEncoding) o;

 		out.writeObject( "meta.expression", enc._expression );
		out.writeObject( "meta.name", enc._encoding );

    }
    
    public void doWrite(TypeOutputStream out, Object o) throws TypeException, IOException
    {
        
    }

    public Object doRead(TypeInputStream in) throws TypeException, IOException
    {
        byte[] data = (byte[]) _expression.doRead( in );
        return new String( data, _encoding );
    }
}
