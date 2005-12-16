package com.abc.bookstore.dictionary;

import java.io.InputStream;

public class DictionaryLoader
{
	private static DictionaryLoader instance;
	
	public static InputStream getDictionaryStream( String name )
	{
		if ( instance == null )
		{
			instance = new DictionaryLoader();
		}
		return instance.getStream(name);
	}
	
	private InputStream getStream( String name )
	{
		return this.getClass().getResourceAsStream( name );
	}
}
