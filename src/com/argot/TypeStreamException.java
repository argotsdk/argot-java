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
		System.out.println(buffer);
		return buffer.toString();
	}
}
