
package com.abc.bookstore;


public class Book
{	
	private String ISBN; 
	private String title; 
	private String description; 
	private String author; 
	
	public Book( String ISBN, String title, String description, String author )
	{
		this.ISBN = ISBN;
		this.title = title;
		this.author = author;
		this.description = description;
	}
	
    public String getAuthor()
    {
        return author;
    }

    public String getDescription()
    {
        return description;
    }

    public String getISBN()
    {
        return ISBN;
    }

    public String getTitle()
    {
        return title;
    }

    public void setAuthor(String string)
    {
        author = string;
    }

    public void setDescription(String string)
    {
        description = string;
    }

    public void setISBN(String string)
    {
        ISBN = string;
    }

    public void setTitle(String string)
    {
        title = string;
    }

}
