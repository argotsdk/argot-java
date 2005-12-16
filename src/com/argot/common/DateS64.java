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
import java.util.Date;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class DateS64
implements TypeReader, TypeWriter
{

	public static final String TYPENAME = "date.java";
	
	public void write(TypeOutputStream out, Object o, TypeElement element)
		throws TypeException, IOException
	{
		Date d = (Date) o;
		if ( d == null )
			out.writeObject( BigEndianSignedLong.TYPENAME, new Long( Long.MIN_VALUE ) );
		else
			out.writeObject( BigEndianSignedLong.TYPENAME, new Long( d.getTime() ) );
	}

    public Object read(TypeInputStream in, TypeElement element) throws TypeException, IOException
    {
        throw new TypeException("not implemented");
    }

}
