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
package com.argot;

import java.io.IOException;

import com.argot.meta.MetaName;

public class TypeWriterInvalid 
implements TypeLibraryWriter, TypeBound
{
	MetaName _name;

    public void write(TypeOutputStream out, Object o)
    throws TypeException, IOException
    {
        throw new TypeException("TypeWriter invalid - no writer for " + _name );
    }

	public TypeWriter getWriter(TypeMap map) 
	throws TypeException 
	{
        throw new TypeException("TypeWriter invalid - no writer for " + _name );
	}

	public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
	throws TypeException 
	{
		_name = library.getName( definitionId );
	}

}
