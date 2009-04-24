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
package com.argot.common;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class U8Utf8 
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "u8utf8";
	public static final String VERSION = "1.3";

	public Object read(TypeInputStream in)
	throws TypeException, IOException
	{
		int id = in.read();

		if ( id > 0 )
		{
			byte[] bytes = new byte[ id ];
			in.read(bytes,0,bytes.length);
			return new String( bytes, "UTF-8");
		}
		
		return new String("");
	}

	public void write(TypeOutputStream out, Object o )
	throws TypeException, IOException
	{
		if ( !(o instanceof String) )
		{
			if ( o == null )
			{
				out.getStream().write( 0 );
				return;
			}
			else
			{
				throw new TypeException("u8ascii requires string. received: " + o.getClass().getName());
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