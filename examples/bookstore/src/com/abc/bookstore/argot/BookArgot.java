package com.abc.bookstore.argot;

/**
 * The Argot marshalling routines for a book.  The TypeReader is
 * handled by the TypeReaderAuto marshalling routine.
 */
import java.io.IOException;

import com.abc.bookstore.Book;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;


public class BookArgot
implements TypeWriter
{
	public static final String TYPENAME = "book";

    public void write(TypeOutputStream out, Object o, TypeElement element)
    throws TypeException, IOException
    {
    	Book book = (Book) o;
    	 
		out.writeObject("u8ascii", book.getISBN() );
		out.writeObject("u8ascii", book.getTitle() );
		out.writeObject("u8ascii", book.getDescription());
		out.writeObject("u8ascii", book.getAuthor());
    }
}
