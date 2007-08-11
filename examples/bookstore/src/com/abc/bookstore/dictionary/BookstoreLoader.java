package com.abc.bookstore.dictionary;

import com.abc.bookstore.Book;
import com.abc.bookstore.argot.BookArgot;
import com.abc.bookstore.argot.BookArrayArgot;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeReaderAuto;

public class BookstoreLoader
extends ResourceDictionaryLoader
{
	public static final String DICTIONARY = "bookstore.dictionary";
	
	public BookstoreLoader()
	{
		super(DICTIONARY);
	}
	
	public String getName()
	{
		return DICTIONARY;
	}
	
	public void bind( TypeLibrary library ) throws TypeException
	{
		library.bind( BookArgot.TYPENAME, new TypeReaderAuto( Book.class ), new BookArgot(), Book.class );
		library.bind( BookArrayArgot.TYPENAME, new BookArrayArgot(), new BookArrayArgot(), Book[].class );
	}
}
