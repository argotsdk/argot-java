package com.argot.meta;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeReader;

public interface MetaExpressionReader
{
	public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
	throws TypeException;
}
