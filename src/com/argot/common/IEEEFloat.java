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

		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			byte bytes[] = new byte[4];
			in.getStream().read(bytes,0,4);
			int value = (((bytes[0] & 0xff) << 24) | ((bytes[1] & 0xff) << 16) | ((bytes[2] & 0xff) << 8) | (bytes[3] & 0xff ));			
			return new Float(Float.intBitsToFloat(value));
		}
		
	}

	public static class Writer
	implements TypeWriter
	{

		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			byte[] bytes = new byte[4];
			int s;
			
			if ( o instanceof Float )
			{
			    s = Float.floatToIntBits(((Float) o).floatValue());
			}
			else
			{
				throw new TypeException( "ieee.float: requires Float. " );
			}
			
			bytes[0] = (byte)((s >> 24) & 0xff );
			bytes[1] = (byte)((s >> 16) & 0xff );
			bytes[2] = (byte)((s >> 8) & 0xff );
			bytes[3] = (byte)(s & 0xff );
			
			out.getStream().write(bytes,0,4);
		}
		
	}
	
}
