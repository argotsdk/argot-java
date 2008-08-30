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

import com.argot.TypeException;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.UInt16;
import com.argot.common.UInt8;

public class MetaFixedWidth
extends MetaExpression
implements MetaDefinition
{
	public static String TYPENAME = "meta.fixed_width";
		
	private int _width;
	private MetaFixedWidthAttribute[] _attributes;
	
	public MetaFixedWidth( int width, MetaFixedWidthAttribute[] attributes )
	{
		_width = width;
		_attributes = attributes;
	}

	public MetaFixedWidth( int width, Object[] attributes )
	{
		_width = width;
		_attributes = new MetaFixedWidthAttribute[attributes.length];
		for (int x=0; x<attributes.length;x++)
		{
			_attributes[x] = (MetaFixedWidthAttribute)attributes[x];
		}
		
	}	
    public String getTypeName()
    {
        return TYPENAME;
    }

    public static class MetaBasicTypeWriter
    implements TypeLibraryWriter,TypeWriter
    {
		public void write(TypeOutputStream out, Object o ) 
		throws TypeException, IOException
		{
			MetaFixedWidth tb = (MetaFixedWidth) o;
			
			out.writeObject( UInt16.TYPENAME, new Integer( tb._width ));
			
			// This could be replaced with an array marshaller.
			out.writeObject( UInt8.TYPENAME, new Short( (short) tb._attributes.length ));
			for (int x=0; x<tb._attributes.length; x++)
			{
				out.writeObject( MetaFixedWidthAttribute.TYPENAME, tb._attributes[x]);
			}
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }


}
