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

import java.io.IOException;

import com.argot.meta.MetaExpression;
import com.argot.meta.MetaSequence;

public class TypeReaderAuto
implements TypeReader
{
	private TypeConstructorAuto _constructor;
	
	public TypeReaderAuto( Class clss )
	{
		_constructor = new TypeConstructorAuto( clss );
	}
	
	public Object read(TypeInputStream in, TypeElement element)
	throws TypeException, IOException
	{
		if ( !( element instanceof MetaExpression))
		{
			throw new TypeException( "TypeReaderAuto: required TypeDefinition to read data.");
		}
	
		MetaExpression metaDefinition = (MetaExpression) element;
		if ( metaDefinition instanceof MetaSequence )
		{
			MetaSequence sequence = (MetaSequence) metaDefinition;
			Object[] objects = (Object[]) sequence.doRead( in );
			return _constructor.construct( objects );						
		}
		else
		{
			throw new TypeException( "TypeReaderAuto: can not read unknown type");
		}
		
	}

}
