package com.argot.meta;

import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public interface MetaExpressionResolver
{
	public TypeReader getExpressionReader( TypeMap map, MetaExpression expression )
	throws TypeException;

	public TypeWriter getExpressionWriter( TypeMap map, MetaExpression expression )
	throws TypeException;
}
