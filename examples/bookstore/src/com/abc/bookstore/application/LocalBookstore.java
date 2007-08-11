
package com.abc.bookstore.application;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.abc.bookstore.IBookstore;
import com.abc.bookstore.Bookstore;
import com.abc.bookstore.dictionary.BookstoreLoader;
import com.argot.TypeException;
import com.argot.TypeLibraryLoader;
import com.argot.TypeLibrary;
import com.argot.common.CommonLoader;
import com.argot.dictionary.DictionaryLoader;
import com.argot.meta.MetaLoader;

public class LocalBookstore
{
		
	/**
	 * The Argot type system must be primed on start up of the application.
	 * 
	 * Two data dictionary files are loaded.  The common.dictionary contains
	 * various basic data types such as u8ascii.  The bookstore contains
	 * our applications book and booklist type descriptions.
	 * 
	 * After the dictionaries are loaded the marshalling functions are bound
	 * to the data type.  The Book only binds the TypeWriter.  The reading is
	 * handled automatically by the TypeReaderAuto marshaller.
	 * 
	 */
	private static void setupArgot() throws TypeException, IOException
	{
		TypeLibraryLoader libraryLoaders[] = {
				new MetaLoader(),
				new DictionaryLoader(),
				new CommonLoader(),
				new BookstoreLoader()
			};
		
		TypeLibrary library = new TypeLibrary( libraryLoaders );
	}
	
	
	public static void main( String[] args )
	{
        try
        {
			setupArgot();
		
			IBookstore bookstore = new Bookstore();
		
			CommandBookstore command = new CommandBookstore( bookstore );
		
			InputStreamReader isr = new InputStreamReader( System.in );
			BufferedReader reader = new BufferedReader( isr );
		
			command.getCommand( System.out, reader );
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
 		
	}
}
