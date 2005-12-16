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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

/**
 * This is a short string encoded in UTF8 format. It uses a single unsigned
 * byte to specify the length.  So the data length can be between 0-254 
 * characters. 0 is an empty string.  255 is a null string.
 */
public class U8Ascii
implements TypeReader, TypeWriter
{

	public static final String TYPENAME = "u8ascii";

	public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException
	{
		int id = in.getStream().read();
		if ( id == -1 ) throw new IOException( "disconnected");
		if ( id > 0 )
		{
			byte[] bytes = new byte[ id ];
			in.getStream().read(bytes);
			return new String( bytes, "US-ASCII");
		}
		
		return new String("");
	}

	public void write(TypeOutputStream out, Object o, TypeElement element )
		throws TypeException, IOException
	{
		if ( !(o instanceof String) )
		{
			if ( o == null )
			{
				out.getStream().write( 0 );
				return;
			}		
		}

		byte[] bytes = ((String) o).getBytes();
		int size = bytes.length;
		
		if ( size > 255 ) 
			throw new TypeException( "u8ascii: String length exceeded max length of 255.  len =" + size );
		
		out.getStream().write( size );
		out.getStream().write( bytes, 0, size );
	}

}
