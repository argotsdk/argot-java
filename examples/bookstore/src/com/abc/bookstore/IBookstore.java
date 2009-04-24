
package com.abc.bookstore;

import java.io.IOException;

import com.argot.TypeException;


public interface IBookstore
{
	public static final String TYPENAME = "bookstore";

    public boolean addBook(Book book);
    
    public void removeBook(String isbn) 
    throws BookNotFoundException;
    
    public Book[] findBooksByAuthor(String name);
    
    public Book getBookByIsbn(String isbn)
    throws BookNotFoundException;
        
    public Book[] getAllBooks();
    
    public void loadBooks( String filename )
    throws IOException, TypeException;
    
    public void saveBooks( String filename )
    throws IOException, TypeException;
}