package com.argot;

public interface TypeBound 
{
	public void bind( TypeLibrary library, TypeElement definition, String typeName, int typeId ) 
	throws TypeException;
}
