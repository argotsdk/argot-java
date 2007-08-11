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
 
package com.argot.network;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;

public class NetArgotLoader
extends ResourceDictionaryLoader
{
	public static final String DICTIONARY = "netargot.dictionary";
	
	public NetArgotLoader()
	{
		super( DICTIONARY );
	}
	
	public String getName()
	{
		return DICTIONARY;
	}

	public void bind( TypeLibrary library ) throws TypeException
	{
		// do nothing.
	}


}
