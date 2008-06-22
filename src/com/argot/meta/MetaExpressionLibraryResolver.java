package com.argot.meta;

import com.argot.TypeException;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaExpressionLibraryResolver 
implements MetaExpressionResolver
{
	public TypeReader getExpressionReader( TypeMap map, MetaExpression expression ) 
	throws TypeException
	{
    	TypeLibraryReader reader = map.getLibrary().getReader(expression.getTypeId());
		if(!(reader instanceof MetaExpressionReader))
		{
			throw new TypeException("MetaExpressionReader expected. Found: " + reader.getClass().getName() );
		}
		MetaExpressionReader expressionReader = (MetaExpressionReader) reader;
		return expressionReader.getExpressionReader(map,this, expression);    	
	}

	public TypeWriter getExpressionWriter(TypeMap map, MetaExpression expression)
	throws TypeException
	{
    	TypeLibraryWriter writer = map.getLibrary().getWriter(expression.getTypeId());
		if(!(writer instanceof MetaExpressionWriter))
		{
			throw new TypeException("MetaExpressionWriter expected. Found: " + writer.getClass().getName() );
		}
		MetaExpressionWriter expressionWriter = (MetaExpressionWriter) writer;
		return expressionWriter.getExpressionWriter(map,this, expression);    	
	}
}
