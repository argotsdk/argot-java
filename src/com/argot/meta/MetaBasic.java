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
import com.argot.common.BigEndianUnsignedByte;

public class MetaBasic
extends MetaBase
implements TypeReader, TypeWriter, MetaDefinition
{
	public static String TYPENAME = "meta.basic";
		
	private short _width;
	private short _flags;
	
	public MetaBasic( short width, short flags )
	{
		_width = width;
		_flags = flags;
	}

	public MetaBasic( int width, int flags )
	{
		_width = (short) width;
		_flags = (short) flags;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
	public Object read(TypeInputStream in, TypeElement element ) 
	throws TypeException, IOException
	{		
		if ( element instanceof MetaExpression )
		{
			TypeReader reader = new TypeReaderAuto( this.getClass() );
			return reader.read( in, element );
		}
		throw new TypeException( "shouldn't get here.");		
	}

	public void write(TypeOutputStream out, Object o, TypeElement element ) 
	throws TypeException, IOException
	{
		MetaBasic tb = (MetaBasic) o;
		
		out.writeObject( BigEndianUnsignedByte.TYPENAME, new Short( tb._width ));
		out.writeObject( BigEndianUnsignedByte.TYPENAME, new Short( tb._flags ));
	}


    public void doWrite(TypeOutputStream out, Object o) throws TypeException, IOException
    {
    }


    public Object doRead(TypeInputStream in) throws TypeException, IOException
    {
        return null;
    }

}
