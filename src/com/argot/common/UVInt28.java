package com.argot.common;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;


/*
 * UVarInt.  Unsigned Variable Length Integer.
 * 
 * Encodes a variable length unsigned integer up to 2^28.  Uses a common technique of using
 * the MSB as a flag to indicate a continuation. This version will decode a maximum
 * of four bytes.
 * 
 * A value between 0-127 will be encoded in a single byte.  128 to 2^14 will be encoded in
 * two bytes.
 * 
 * (meta.entry
 * 		(library.definition meta.name:"uvarint" meta.version:"1.0")
 *      (meta.atomic [
 *      	(meta.atomic.integer)
 *      	(meta.atomic.unsigned max_base:"28")
 *      ])
 * 
 */

public class UVInt28
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "uvint28";  
	public static final String VERSION = "1.3";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		int a;
		int value = 0;
		
		a = in.getStream().read();
		
		while ( true )
		{
			value = value + (a & 0x7F);
			
			if ((a & 0x80) > 1)
			{
				value <<= 7;
				
				a = in.getStream().read();
			}
			else
			{
				break;
			}
		}
		
		return new Integer( value );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{	
		int s;
		
		if ( o instanceof Long )
		{
		    s = ((Long) o).intValue();
		}
		else if ( o instanceof Integer )
		{
		    s = ((Integer)o).intValue();
		}
		else
		{
			throw new TypeException( "uvint28: requires Integer or Long" );
		}
		
		if (s<0 || s>((1<<28)-1))
		{
			throw new TypeException("uvint28: value out of range - " + s );
		}
		
		// if the value is small write single byte and finish.
		// otherwise more complex encoding is required.
		if (s<128)
		{
			out.getStream().write( (int) s );
		}
		else
		{
			int shift = 21;
			boolean found = false;
			
			while (shift > 0)
			{
				int mask = 0x7f << shift;
				int value = (int) ((s & mask) >> shift);
				if ( value > 0 || found )
				{
					value |= 0x80;
					
					found = true;
					//System.out.println("Writing Value: " + Integer.toBinaryString(value));
					out.getStream().write(value);
				}
				
				shift-=7;
			}
			
			//System.out.println("Writing Value: " + Integer.toBinaryString(s & 0x7F));
			out.getStream().write( (int)(s & 0x7F));
		}
		
	}
	
}
