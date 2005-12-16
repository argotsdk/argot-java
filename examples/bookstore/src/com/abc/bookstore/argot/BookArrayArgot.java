package com.abc.bookstore.argot;

/**
 * The Argot marshalling routines for a booklist.
 * 
 * A maximum of 256 books can be stored as only a u8
 * is used for the array length.
 * 
 */
import java.io.IOException;

import com.abc.bookstore.Book;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class BookArrayArgot
implements TypeWriter, TypeReader
{
	public static final String TYPENAME = "booklist";

    public void write(TypeOutputStream out, Object o, TypeElement element)
    throws TypeException, IOException
    {
 		Book[] books = (Book[]) o;
 		
 		out.writeObject( "u8", new Integer(books.length));
 		
 		for(int x=0; x< books.length; x++)
 		{
 			out.writeObject("book", books[x]);
 		}
    }

    public Object read(TypeInputStream in, TypeElement element)
    throws TypeException, IOException
    {
    	Short size = (Short) in.readObject( "u8");
    	Book[] books = new Book[size.intValue()];
    	for(int x=0; x<size.intValue(); x++)
    	{
    		books[x] = (Book) in.readObject("book" );
    	}
    	return books;
    }

}
