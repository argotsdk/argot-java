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

import java.util.Stack;

public class TypeStreamException
extends TypeException
{
	private static final long serialVersionUID = 2156978146821573623L;
	
	private Stack _stack;
	
	public TypeStreamException( String reason )
	{
		super(reason);
		
		_stack = new Stack();
		_stack.push( reason );
	}

	public TypeStreamException( String reason, Throwable cause )
	{
		super(reason,cause);

		_stack = new Stack();
		_stack.push( reason );
	}
	
	public void addTypeName( String type )
	{
		_stack.push( type );
	}

	public String getMessage()
	{
		StringBuffer buffer = new StringBuffer();
		for ( int x=_stack.size()-1; x >=0; x-- )
		{
			buffer.append( _stack.get(x));
			if ( x > 0 )
				buffer.append( ">" );
		}
		return buffer.toString();
	}
}
