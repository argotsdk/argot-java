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
import java.util.Date;

import com.argot.TypeException;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class DateS64
implements TypeLibraryReader, TypeLibraryWriter
{

	public static final String TYPENAME = "date.java";
	public static final String VERSION = "1.3";
	
	private static class DateS64Writer
	implements TypeWriter
	{
		private TypeWriter _int64;
		
		public DateS64Writer( TypeWriter int64 )
		{
			_int64 = int64;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException
		{
			Date d = (Date) o;
			if ( d == null )
				_int64.write( out, new Long( Long.MIN_VALUE ) );
			else
				_int64.write( out, new Long( d.getTime() ) );
		}
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
        throw new TypeException("not implemented");
    }

	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
		return new DateS64Writer(map.getWriter(map.getStreamId(Int64.TYPENAME)));
	}
}
