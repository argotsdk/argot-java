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

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.auto.TypeReaderAuto;

public class MetaExpressionReaderAuto 
implements TypeLibraryReader, TypeBound
{
	TypeReaderAuto _auto;
	
	public MetaExpressionReaderAuto(Class clss)
	{
		_auto = new TypeReaderAuto(clss);
	}

	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		_auto.bind(library, definitionId, definition);
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return _auto.getReader(map);
	}	
}
