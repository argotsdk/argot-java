/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

package com.argot.meta;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.auto.TypeBeanMarshaller;
import com.argot.auto.TypeReaderAuto;

public class MetaExtensionLoader
extends ResourceDictionaryLoader
{
	public static final String DICTIONARY = "meta_extensions.dictionary";	
	
	public MetaExtensionLoader()
	{
		super(DICTIONARY);
	}
		
	public String getName()
	{
		return DICTIONARY;
	}

	@Override
	public void bind(TypeLibrary library) throws TypeException
	{
	
		library.bind( 
			library.getTypeId( DictionaryName.TYPENAME, "1.3"), 
			new TypeReaderAuto(DictionaryName.class), 
			new TypeBeanMarshaller(), 
			DictionaryName.class );
		
	    library.bind( 
	    	library.getTypeId(MetaFixedWidthAttributeSigned.TYPENAME, "1.3"),
	    	new TypeReaderAuto(MetaFixedWidthAttributeUnsigned.class), 
	    	new TypeBeanMarshaller(), 
	    	MetaFixedWidthAttributeSigned.class );	    

	    library.bind( 
    		library.getTypeId(MetaFixedWidthAttributeIEEE756.TYPENAME, "1.3"),
    		new TypeReaderAuto(MetaFixedWidthAttributeIEEE756.class), 
    		new TypeBeanMarshaller(), 
    		MetaFixedWidthAttributeIEEE756.class );
	    
	}

}
