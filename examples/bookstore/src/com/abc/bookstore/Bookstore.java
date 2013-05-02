package com.abc.bookstore;

/*
 * This provides a very simple implementation of the bookstore
 * interface. 
 */

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeInputStream;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;


public class Bookstore 
implements IBookstore
{
	private List<Book> books;
	private Map<String,Book> isbnMap;
	private TypeLibrary library;
		
	public Bookstore( TypeLibrary library )
	{
		books = new ArrayList<Book>();
		isbnMap = new HashMap<String,Book>();
		this.library = library;
	}
	
	
	/**
	 * The load books provides a very basic example of how the Argot
	 * TypeInputStream is used.  
	 * 
	 * A booklist type is red from the selected filename.
	 */
	public void loadBooks(String filename) throws TypeException, IOException
	{
		// Create a map of data types required for this file.
		TypeMap typeMap = getDataFileTypeMap();
		   	
		// Create a typed input stream using file and type map.
		TypeInputStream typeInputStream = new TypeInputStream( new FileInputStream( filename ), typeMap );
        
		// The file structure is defined by booklist.  Read the books.
		Book[] books = (Book[]) typeInputStream.readObject( "booklist" );
        
		// Add the books to our book list.
		for ( int x=0; x< books.length;x++)
		{
			addBook( books[x]);
		}
	}


	/**
	 * The save books method is the exact reverse of the read books.
	 * An array of books is passed to Argot to be written to file.
	 */
	public void saveBooks(String filename) 
	throws TypeException, IOException
	{
		FileOutputStream outputStream = new FileOutputStream( filename );
		TypeMap typeMap = getDataFileTypeMap();
		TypeOutputStream typeOutputStream = new TypeOutputStream( outputStream, typeMap );
		typeOutputStream.writeObject( "booklist",  getAllBooks() );
	}
    
    
	/**
	 * The TypeMap is a central concept to Argot.  It maps selected
	 * data types from the internal system to the external system.
	 * In this case only a small number of data types is used in the 
	 * external data file used to store the book list. 
	 */
	private TypeMap getDataFileTypeMap() 
	throws TypeException
	{
		TypeMap map = new TypeMap( library, new TypeMapperError() );
		map.map( 1, library.getDefinitionId( "book", "1.0" ) );
		map.map( 2, library.getDefinitionId( "booklist", "1.0" ));
		map.map( 3, library.getDefinitionId( "u8ascii", "1.3"));
		map.map( 4, library.getDefinitionId( "uint8", "1.3"));
		
		// TODO The following mappings are required due to the current design of 
		// MetaExpressionLibraryResolver.  This needs to be refactored to use
		// a different method to pickup the correct readers for parts of expressions.
		map.map( 5, library.getDefinitionId( "meta.reference", "1.3"));
		map.map( 6, library.getDefinitionId( "meta.tag", "1.3"));
		return map;    	
	}


    // The rest of the bookstore implementation follows.
	
	public boolean addBook( Book book )
	{
		Book bookCheck = (Book) isbnMap.get( book.getISBN() );
		if ( bookCheck != null )
			return false;
			
		isbnMap.put( book.getISBN(), book );
		books.add( book );
		return true;
	}
	
	public void removeBook( String isbn )
	throws BookNotFoundException
	{
		Book book = getBookByIsbn(isbn);
		books.remove(book);
		isbnMap.remove( isbn );
	}
	
	public Book[] findBooksByAuthor( String name )
	{
		List<Book> results = new ArrayList<Book>();
		
		for (int x=0; x< books.size();x++ )
		{
			Book book = (Book) books.get(x);
			if ( book.getAuthor().equals(name))
			{
				results.add( book );
			}
		}

		Book[] books = new Book[results.size()];
		return (Book[]) results.toArray(books);
	}
	
	public Book getBookByIsbn( String isbn )
	throws BookNotFoundException
	{
        Book book = (Book) isbnMap.get( isbn );
        if (book==null)
        	throw new BookNotFoundException( isbn );
        return book;
	}
	
	public Book[] getAllBooks()
	{
		Book[] allBooks = new Book[books.size()];
		return (Book[]) books.toArray(allBooks);
	}
 

}
