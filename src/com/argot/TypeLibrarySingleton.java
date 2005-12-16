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
package com.argot;

import com.argot.dictionary.DictionaryMap;

/**
 * Singleton to allow applications to retrieve a central
 * TypeLibrary across a whole application.
 */
public class TypeLibrarySingleton
{

	private static TypeLibrary _typeLibrary;
	
	public static TypeLibrary getDefault()
	throws TypeException
	{
		if ( _typeLibrary == null )
		{
			_typeLibrary = new TypeLibrary();
			TypeMapCore.loadLibrary( _typeLibrary );
			DictionaryMap.loadDictionaryMap( _typeLibrary );
			TypeBindCommon.bindCommon( _typeLibrary );
		}
		return _typeLibrary;
	}


}
