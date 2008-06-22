package com.argot.meta;

public class MetaFixedWidthAttributeSize 
extends MetaFixedWidthAttribute
{
	public static final String TYPENAME = "meta.fixed_width.attribute.size";
	
	private short _size;
	
	public MetaFixedWidthAttributeSize( short size )
	{
		_size = size;
	}
	
	public short getSize()
	{
		return _size;
	}
	
	public void setSize( short size )
	{
		_size = size;
	}
}
