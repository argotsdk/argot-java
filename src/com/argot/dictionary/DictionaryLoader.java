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
import com.argot.TypeMap;
import com.argot.TypeReaderAuto;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaEnvelop;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaMap;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;

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
		MetaDefinition meDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference(library.getId("meta.expression"),"size"),
				new MetaReference(library.getId("meta.expression"),"type")
			}
		);

		library.register( "meta.envelop", meDef, new TypeReaderAuto(MetaEnvelop.class), new MetaEnvelop.MetaEnvelopTypeWriter(), MetaEnvelop.class );
		
	
		MetaMap exprRefDef = new MetaMap( library.getId("meta.definition"), library.getId("meta.envelop"));		
		library.register( "meta.definition#envelop", exprRefDef, new MetaMarshaller(),new MetaMarshaller(), null );
		MetaDefinition dDef =
				new MetaEnvelop(
					new MetaReference(library.getId("u16"),"size" ),
					new MetaReference(library.getId("meta.definition"),"definition")
				);			  
	

		library.register( "dictionary.definition",  dDef, new MetaMarshaller(), new MetaMarshaller(), null );

		MetaDefinition tDef =new MetaSequence(
			new MetaExpression[] {
				new MetaReference(library.getId("u16"),"id" ),
				new MetaReference(library.getId("meta.name"),"name"),
				new MetaReference(library.getId("dictionary.definition"),"definition")				  
			}
	    );

	    library.register( "dictionary.entry", tDef, new MetaMarshaller(), new MetaMarshaller(), null );

	    TypeMapMarshaller tmm = new TypeMapMarshaller();
		
		MetaDefinition dmDef =	new MetaSequence(
			new MetaExpression[] {
				new MetaArray(
					new MetaReference(library.getId("u16"),"size" ),
					new MetaReference(library.getId("dictionary.entry"),"word")
				)				  
			}
		);
		
		library.register( "dictionary.map", dmDef, new TypeReaderAuto(TypeMap.class), tmm, null );
		
		MetaDefinition dWords =
				new MetaEnvelop(
					new MetaReference(library.getId("u16"),"size" ),
					new MetaReference(library.getId("dictionary.map"),"words")
				);			  
	

		library.register( "dictionary.words", dWords, new MetaMarshaller(), new MetaMarshaller(), null );
			
    }

}
