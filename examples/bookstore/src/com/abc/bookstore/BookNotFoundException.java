package com.abc.bookstore;

public class BookNotFoundException 
extends Exception
{

	private static final long serialVersionUID = -7441845573853619657L;


	public BookNotFoundException(String reason)
    {
        super(reason);
    }


    public BookNotFoundException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

}
