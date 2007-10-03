/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

public class IEEEFloat 
{
	public static final String TYPENAME = "ieee.float";
		
	public static class Reader
	implements TypeReader
	{

		public Object read(TypeInputStream in, TypeElement element)
		throws TypeException, IOException 
		{
			int a,b,c,d;
			
			a = in.getStream().read();
			b = in.getStream().read();
			c = in.getStream().read();
			d = in.getStream().read();
			
			int value = ((a << 24) + (b << 16) + (c << 8) + d);

			return new Float(Float.intBitsToFloat(value));
		}
		
	}

	public static class Writer
	implements TypeWriter
	{

		public void write(TypeOutputStream out, Object o, TypeElement element)
		throws TypeException, IOException 
		{
			int a,b,c,d;
			
			int s;
			
			if ( o instanceof Float )
			{
			    s = Float.floatToIntBits(((Float) o).floatValue());
			}
			else
			{
				throw new TypeException( "ieee.float: requires Float. " );
			}
						
			a = (int)(s & (long) 0xff000000) >> 24;
			b = (int)(s & 0x00ff0000) >> 16;
			c = (int)(s & 0x0000ff00) >> 8;
			d = (int)(s & 0x000000ff);
			
			out.getStream().write( a );
			out.getStream().write( b );
			out.getStream().write( c );
			out.getStream().write( d );

		}
		
	}
	
}
