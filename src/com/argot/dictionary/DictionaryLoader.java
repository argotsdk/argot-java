/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
 *
 * This software is licensed under the Argot Public License 
 * which may be found in the file LICENSE distributed 
 * with this software.
 *
 * More information about this license can be found at
 * http://www.einet.com.au/License
 * 
 * The Developer of this software is Live Media Pty Ltd,
 * PO Box 4591, Melbourne 3001, Australia.  The license is subject 
 * to the law of Victoria, Australia, and subject to exclusive 
 * jurisdiction of the Victorian courts.
 */

package com.argot.dictionary;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.common.UInt16;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaEnvelop;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaReference;

public class DictionaryLoader
implements TypeLibraryLoader
{
	public static final String DICTIONARY = "dictionary.dictionary";
	
	public String getName()
	{
		return DICTIONARY;
	}

	public void load( TypeLibrary library ) throws TypeException
	{

		
		MetaDefinition dWords =
				new MetaEnvelop(
					new MetaReference(library.getId(UInt16.TYPENAME) ),
					new MetaReference(library.getId("dictionary.entry.list"))
				);			  
	

		library.register( "dictionary.words", dWords, new MetaMarshaller(), new MetaMarshaller(), null );
			
    }

}
