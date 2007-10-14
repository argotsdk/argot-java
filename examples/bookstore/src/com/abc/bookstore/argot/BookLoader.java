package com.abc.bookstore.argot;

import com.abc.bookstore.Book;
import com.abc.bookstore.BookNotFoundException;
import com.abc.bookstore.Bookstore;
import com.abc.bookstore.IBookstore;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeReaderAuto;
import com.argot.auto.TypeArrayMarshaller;
import com.argot.auto.TypeBeanMarshaller;
import com.argot.remote.MetaInterface;
import com.argot.remote.MetaRemoteException;


public class BookLoader 
extends ResourceDictionaryLoader
{
	public final static String DICTIONARY = "bookstore.dictionary";
	
	public BookLoader()
	{
		super( DICTIONARY );
	}
	
	public String getName()
	{
		return DICTIONARY;
	}
	
	public void bind( TypeLibrary library ) 
	throws TypeException
	{
		library.bind( "book", new TypeBeanMarshaller(), new TypeBeanMarshaller(), Book.class );
		library.bind( "booklist", new TypeArrayMarshaller(), new TypeArrayMarshaller(), Book[].class );
	}
}
