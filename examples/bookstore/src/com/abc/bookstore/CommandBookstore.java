package com.abc.bookstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;

import com.argot.TypeException;

public class CommandBookstore
{
	private IBookstore bookstore;
	
	public CommandBookstore( IBookstore bookstore )
	{
		this.bookstore = bookstore;
	}
	
	public void getCommand( PrintStream out, BufferedReader in )
	{
		printHelp(out);
		
		while( true )
		{
			
            try
            {
				out.print("> ");
                String commandLine = in.readLine();
                processCommand( out, in, commandLine );
            }
            catch (IOException e)
            {
				System.out.println("Command Failed:" + e.getMessage() );
				e.printStackTrace();
            }
            catch (TypeException e)
            {
    			System.out.println("Command Failed:" + e.getMessage() );
    			e.printStackTrace();
            }
		}
	}

	private void processCommand( PrintStream out, BufferedReader in, String commandLine ) 
	throws IOException, TypeException
	{
		String[] args = commandLine.split(" ");
		if ( args.length == 0 )
			return;

		String command = args[0];
			
		if ( "quit".equals(command) )
		{
			System.exit(0);
			return;
		}
		else if ( "help".equals(command))
		{
			printHelp(out);
		}
		else if ( "list".equals(command))
		{
			Book[] books = bookstore.getAllBooks();
			if ( books.length == 0 )
			{
				out.println("bookstore empty");
				return;
			}
				
			for ( int x=0; x<books.length; x++ )
			{
				Book book = books[x];
				out.println( "'" + book.getTitle() + "' by " + book.getAuthor() +" (ISBN:" + book.getISBN() + ")");
				out.println( "description: " + book.getDescription() );
			}
		}
		else if ( "save".equals(command) && args.length == 2 )
		{
			bookstore.saveBooks( args[1] );
		}
		else if ( "load".equals(command) && args.length == 2 )
		{
			bookstore.loadBooks( args[1] );
		}
		else if ( "find".equals(command) && args.length == 2 )
		{
			Book[] books = bookstore.findBooksByAuthor( args[1] );
			if (books.length == 0 )
			{
				out.println( "no books by selected author");
				return;
			}

			for ( int x=0; x<books.length; x++ )
			{
				Book book = books[x];
				out.println( "'" + book.getTitle() + "' by " + book.getAuthor() +" (ISBN:" + book.getISBN() + ")");
				out.println( "description: " + book.getDescription() );
			}

		}
		else if ( "delete".equals(command) && args.length == 2 )
		{
			try
			{
				Book book = bookstore.getBookByIsbn(args[1]);
				bookstore.removeBook( book.getISBN() );
				out.println("book deleted");
			}
			catch (BookNotFoundException e)
			{
				out.println( "book not found");
			}
		}
		else if ( "add".equals(command))
		{
			out.print("isbn:");
			String isbn = in.readLine();
			out.print("title:");
			String title = in.readLine();
			out.print("author:");
			String author = in.readLine();
			out.print("description:");
			String description = in.readLine();
				
			Book book = new Book( isbn, title, author, description );
			if (bookstore.addBook( book ))
			{
				out.println( "book added" );
			}
			else
			{
				out.println( "book not accepted.  Duplicate ISBN?" );
			}
		}
		else
		{
			if ( !command.trim().equals("") )
				out.println("unrecognised command 'help' for details");
		}
		
	}

    private void printHelp(PrintStream out)
    {
        out.println("quit - finish");
        out.println("help - this help");
        out.println("list - list current books");
        out.println("save <filename> - save current booklist");
        out.println("load <filename> - load a booklist");
        out.println("delete <isbn> - delete a book");
        out.println("add - add a new book");
        out.println("find <author> - list books by author");
    }

}
