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

import com.argot.TypeException;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;

public class MetaAtom
extends MetaExpression
implements MetaDefinition
{
	public static String TYPENAME = "meta.atom";
	public static final String VERSION = "1.3";
		
	private int _min_bit_length;
	private int _max_bit_length;
	private MetaAtomAttribute[] _attributes;
	
	public MetaAtom( int min_bit_length, int max_bit_length, MetaAtomAttribute[] attributes )
	{
		_min_bit_length = min_bit_length;
		_max_bit_length = max_bit_length;
		_attributes = attributes;
	}

	public MetaAtom( int min_bit_length, int max_bit_length, Object[] attributes )
	{
		_min_bit_length = min_bit_length;
		_max_bit_length = max_bit_length;
		
		_attributes = new MetaAtomAttribute[attributes.length];
		for (int x=0; x<attributes.length;x++)
		{
			_attributes[x] = (MetaAtomAttribute)attributes[x];
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
			MetaAtom tb = (MetaAtom) o;
			
			out.writeObject( UVInt28.TYPENAME, new Integer( tb._min_bit_length ));
			out.writeObject( UVInt28.TYPENAME, new Integer( tb._max_bit_length ));
			
			// This could be replaced with an array marshaller.
			out.writeObject( UInt8.TYPENAME, new Short( (short) tb._attributes.length ));
			for (int x=0; x<tb._attributes.length; x++)
			{
				out.writeObject( MetaAtomAttribute.TYPENAME, tb._attributes[x]);
			}
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }


}
