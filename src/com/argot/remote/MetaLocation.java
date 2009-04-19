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

package com.argot.remote;


/**
 * This is an abstract data type.
 * 
 * @author oobles
 *
 */
public interface MetaLocation 
{
	public static final String TYPENAME = "remote.location";
	public static final String VERSION = "1.3";

	public void setHost( String host );
	public String getHost();
}
