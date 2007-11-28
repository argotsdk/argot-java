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

/**
 * This is an empty value.  Some objects are simply an identifier with
 * no values.  Often an object just needs to be identified.  It is a 
 * sequence of a single Empty value.
 */
public class Empty
implements TypeReader, TypeWriter
{
	
	public static final String TYPENAME = "empty";

	public Object read(TypeInputStream in ) 
	throws TypeException, IOException
	{
		// There is nothing to read, as this is an empty value.
		return null;
	}

	public void write(TypeOutputStream out, Object o )
	throws TypeException, IOException
	{
		// There is nothing to write, as this is an empty value.
	}
}
