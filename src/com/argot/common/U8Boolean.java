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

/**
 * The BOOL type is a single U8 character which has the value 0 or 1.
 * if the actual value is anything other than 0 the final object will
 * be true.
 * 
 * It requires/returns a java.lang.Boolean
 */
public class U8Boolean
implements TypeReader, TypeWriter
{
	public static final String TYPENAME = "bool";
	public static final String VERSION = "1.3";
	
	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		int a;
	
		a = in.read();
	
		return new Boolean( a == 0 ? false : true );
	}

	public void write(TypeOutputStream out, Object o ) 
	throws TypeException, IOException
	{
		int a;
		if ( o instanceof Boolean )
		{
			a = ((Boolean) o).booleanValue() == false ? 0 : 1 ;
		}
		else
			throw new TypeException( "U8Boolean requires Boolean object");

		out.getStream().write( a );
	}	
}
