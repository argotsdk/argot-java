package com.argot.meta;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeWriter;

public interface MetaExpressionWriter 
{
	public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
	throws TypeException;
}
