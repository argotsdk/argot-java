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

public class UInt8
implements TypeReader, TypeWriter
{
	public static String TYPENAME = "uint8";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		int i;

		i = in.getStream().read();
		
		if ( i == -1 ) 
			throw new IOException( "EOF" );
		
		return new Short( (short) i );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		if ( o instanceof Integer )
		{
			int b = ((Integer) o).intValue();
			if ( b < 0 || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );
				
			out.getStream().write( b );
		}
		else if ( o instanceof Short )
		{
			int b = ((Short) o).intValue();
			if ( b < 0 || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );

			out.getStream().write( b );
		}
		else if ( o instanceof Byte )
		{
			int b = ((Byte)o).intValue();
			if ( b < MIN || b > MAX )
				throw new TypeException( "U8B: out of range: " + b );
				
			out.getStream().write( b );
		}
	}
	
	public static int MIN = 0;
	public static int MAX = 256-1;

}
