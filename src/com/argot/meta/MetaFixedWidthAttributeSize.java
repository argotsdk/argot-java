package com.argot.meta;

public class MetaFixedWidthAttributeSize 
extends MetaFixedWidthAttribute
{
	public static final String TYPENAME = "meta.fixed_width.attribute.size";
	
	private int _size; // uint16
	
	public MetaFixedWidthAttributeSize( int size )
	{
		_size = size;
	}
	
	public int getSize()
	{
		return _size;
	}
	
	public void setSize( int size )
	{
		_size = size;
	}
}
