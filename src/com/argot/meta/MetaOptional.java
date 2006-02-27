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
import com.argot.TypeWriter;

public class MetaOptional
extends MetaBase
implements MetaExpression
{
	public static String TYPENAME = "meta.optional";

	private MetaExpression _option;

	public MetaOptional( MetaExpression option )
	{
		_option = option;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public MetaExpression getOptionalExpression()
    {
        return _option;
    }

    public static class MetaOptionalTypeReader
    implements TypeReader
    {
		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
		{
			TypeReader reader = new TypeReaderAuto( this.getClass() );
			return reader.read( in, element );
		}
    }
    
    public static class MetaOptionalTypeWriter
    implements TypeWriter
    {
		public void write(TypeOutputStream out, Object o, TypeElement element )
			throws TypeException, IOException
		{
			MetaOptional to = (MetaOptional) o;
			
			out.writeObject( "meta.name", to._option );
		}
    }
    
	public Object doRead(TypeInputStream in )
	throws TypeException, IOException 
	{
		Boolean id = (Boolean) in.readObject( "bool" );
		if ( id.booleanValue() )
		    return _option.doRead( in );
		return null;
	}

	public void doWrite(TypeOutputStream out, Object o)
	throws TypeException, IOException 
	{
	    if ( o == null )
	    {
	        out.writeObject( "bool", new Boolean( false ));
	        return;
	    }
	    
		out.writeObject("bool", new Boolean( true ));
		_option.doWrite( out, o );
	}


}
