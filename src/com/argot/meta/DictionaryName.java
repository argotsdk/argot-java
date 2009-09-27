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
package com.argot.meta;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeLocationName;

public class DictionaryName 
extends DictionaryLocation
implements TypeLocationName
{
	public static final String TYPENAME = "dictionary.name";
	
	private MetaName _name;
	
	public DictionaryName()
	{
		super(TypeLocation.NAME);
	}

	public DictionaryName( MetaName name ) 
	throws TypeException
	{
		super( TypeLocation.NAME );
		_name = name;
	}


	public DictionaryName( TypeLibrary library, String name ) 
	throws TypeException
	{
		this( MetaName.parseName(library, name) );
	}
	
	public MetaName getName()
	{
		return _name;
	}
	
	public void setName(MetaName name)
	{
		_name = name;
	}

}
