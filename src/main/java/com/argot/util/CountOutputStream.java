package com.argot.util;

import java.io.IOException;
import java.io.OutputStream;

public class CountOutputStream 
extends OutputStream 
{
	private int _length = 0;

	@Override
	public void write(int arg0) 
	throws IOException 
	{
		_length++;
	}

	public int length()
	{
		return _length;
	}
}
