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
package com.argot.network;

import java.io.InputStream;
import java.io.OutputStream;

public class TypeEndPointBasic
implements TypeEndPoint
{
	private InputStream _in;
	private OutputStream _out;
	
	public TypeEndPointBasic( InputStream in, OutputStream out )
	{
		_in = in;
		_out = out;
	}
	
	public InputStream getInputStream()
	{
		return _in;
	}
	
	public OutputStream getOutputStream()
	{
		return _out;
	}
}
