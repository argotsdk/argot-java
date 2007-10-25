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
package com.argot.common;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a byte array.  Basically for any binary data or storing
 * other types of data as part of another mapped set.  It uses a
 * single unsigned 32bit integer to specificy the size of the array.
 * It returns and writes byte[].
 */
public class U16ArrayByte
implements TypeReader, TypeWriter
{

	public static final String TYPENAME = "u16binary";
	
	public Object read(TypeInputStream in)
	throws TypeException, IOException
	{
		Integer id = (Integer) in.readObject( BigEndianUnsignedShort.TYPENAME );

		byte[] bytes = new byte[ id.intValue() ];
		in.read(bytes,0,bytes.length);
		
		return bytes;
	}

	public void write(TypeOutputStream out, Object o )
	throws TypeException, IOException
	{
		if ( !(o instanceof byte[]) )
			throw new TypeException( "StringType: can only write objects of type String");

		byte[] bytes = (byte[]) o;

		out.writeObject( "u16", new Integer( bytes.length) );
		out.getStream().write( bytes );
	}
}
