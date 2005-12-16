package com.abc.bookstore;

public class BookNotFoundException 
extends Exception
{
  
    public BookNotFoundException(String reason)
    {
        super(reason);
    }


    public BookNotFoundException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

}
