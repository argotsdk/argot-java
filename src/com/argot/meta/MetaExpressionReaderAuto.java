package com.argot.meta;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeMap;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;

public class MetaExpressionReaderAuto 
implements TypeLibraryReader, TypeBound
{
	TypeReaderAuto _auto;
	
	public MetaExpressionReaderAuto(Class clss)
	{
		_auto = new TypeReaderAuto(clss);
	}

	public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
	throws TypeException 
	{
		_auto.bind(library, definition, typeName, typeId);
	}
	
	public TypeReader getReader(TypeMap map) 
	throws TypeException 
	{
		return _auto.getReader(map);
	}	
}
