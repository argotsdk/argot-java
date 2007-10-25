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

public class IEEEDouble 
{
	public static final String TYPENAME = "ieee.double";	
	
	public static class Reader
	implements TypeReader
	{
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			int a,b,c,d,e,f,g,h;
			
			a = in.getStream().read(); //56  
			b = in.getStream().read(); //48
			c = in.getStream().read(); //40
			d = in.getStream().read(); //32
			e = in.getStream().read(); //24
			f = in.getStream().read(); //16
			g = in.getStream().read(); //8
			h = in.getStream().read(); //0
				
			long value = (((long)a << 56) + ((long)b << 48) + ((long)c << 40) + ((long)d << 32) + ((long)e << 24) + ((long)f << 16) + ((long)g<<8) + (long)h);

			return new Double( Double.longBitsToDouble(value));
		}
	}
	
	public static class Writer
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			int a,b,c,d,e,f,g,h;
			
			if ( !(o instanceof Double) )
				throw new TypeException( "ieee.double: requires Double" );
			
			Double dble = ((Double) o);
			long s = Double.doubleToRawLongBits(dble.doubleValue());
			
			a = (int)((s >> 56) & 0xff);
			b = (int)((s >> 48) & 0xff);
			c = (int)((s >> 40) & 0xff);
			d = (int)((s >> 32) & 0xff);
			e = (int)((s >> 24) & 0xff);
			f = (int)((s >> 16) & 0xff);
			g = (int)((s >> 8) & 0xff);
			h = (int)(s & 0xff);
			
			out.getStream().write( a );
			out.getStream().write( b );
			out.getStream().write( c );
			out.getStream().write( d );
			out.getStream().write( e );
			out.getStream().write( f );
			out.getStream().write( g );
			out.getStream().write( h );			
		}
	}
		
}
