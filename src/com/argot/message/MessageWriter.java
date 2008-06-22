/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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
package com.argot.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.argot.DynamicTypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeMapCore;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.meta.MetaDefinition;

public class MessageWriter
{
    private TypeLibrary _library;
    
    public MessageWriter( TypeLibrary library )
    {
        _library = library;
    }
    
    public void writeMessage( OutputStream out, String type, Object object) throws TypeException, IOException
    {
		
		// write out the dictionary used to write the content.
		TypeMapCore refCore = TypeMapCore.getCoreTypeMap( _library );
		refCore.map( 42, _library.getId("dictionary.words"));
		
		// get the core type map.

		// write out the dictionary used to write the content.
		TypeMapCore core = TypeMapCore.getCoreTypeMap( _library, refCore);
		core.map( 42, _library.getId("dictionary.words"));
	
		// create a dynamic type map.
		DynamicTypeMap dtm = new DynamicTypeMap( _library );
		
		// write out the message content.  Definition of the core type map.
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		TypeOutputStream tmos = new TypeOutputStream( baos , dtm );
		tmos.writeObject( "uint16", new Integer(dtm.getId( _library.getId( type ))));
		tmos.writeObject( type, object );
		baos.close();

		// problem is that in writing the dtm, new types might
		// need to be dynamically added.  Simple solution is to 
		// write it twice.
		core.setReferenceMap( dtm );
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos2 = new TypeOutputStream( baos2 , core );
		tmos2.writeObject( "uint8", new Integer( 1 ));
		tmos2.writeObject( "dictionary.words", dtm );
		baos2.close();
		
		baos2 = new ByteArrayOutputStream(); 
		tmos2 = new TypeOutputStream( baos2 , core );
		tmos2.writeObject( "uint8", new Integer( 1 ));
		tmos2.writeObject( "dictionary.words", dtm );
		baos2.close();
		
		// write out the core used to write the message dictionary.
		core.setReferenceMap( refCore );
		ByteArrayOutputStream baos3 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos3 = new TypeOutputStream( baos3 , core );
		writeCoreMap( tmos3, core );
		baos3.close();
		
		// write out the file.
		out.write( baos3.toByteArray() );
		out.write( baos2.toByteArray() );
		out.write( baos.toByteArray() );      
    }
    
	private static void writeCoreMap( TypeOutputStream out, TypeMapCore map ) throws TypeException, IOException
	{
		// writing out the core and then the extensions.
		out.writeObject( "uint8" , new Integer( 2 ));
		
		// write out the core elements.
		List coreIds = TypeMapCore.getCoreIdentifiers();
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream out1 = new TypeOutputStream( baos1, map );
		
		// write the number of entries.
		out1.writeObject( "uint16", new Integer( coreIds.size() ));
		
			
		Iterator i = coreIds.iterator();	
		while (i.hasNext() )
		{
			int id = ((Integer) i.next()).intValue();	
			String name = map.getName( id );
			TypeElement definition = (TypeElement) map.getStructure(id);
						
			out1.writeObject( "uint16", new Integer(id));
			out1.writeObject( "meta.name", name );
			out1.writeObject( "meta.definition.envelop", definition );
			
		}
		
		baos1.close();
		
		byte[] coreBuffer = baos1.toByteArray();
		out.writeObject( "uint16", new Integer( coreBuffer.length ));
		out.getStream().write( coreBuffer );

		// write out extensions.		
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		TypeOutputStream out2 = new TypeOutputStream( baos2, map );

		// count the number of extensions
		int extensionCount = 0;
		i = map.getIdList().iterator();
		while (i.hasNext() )
		{
			Integer id = (Integer) i.next();
			if ( coreIds.contains(id))
			{
				// already written this out in the core.
				continue;	
			}
			extensionCount++;
		}
		
		out2.writeObject("uint16", new Integer( extensionCount ));
		
		// write out the extensions
		i = map.getIdList().iterator();
		while (i.hasNext() )
		{
			Integer id = (Integer) i.next();
			if ( coreIds.contains(id))
			{
				// already written this out in the core.
				continue;	
			}
			String name = map.getName( id.intValue() );
			MetaDefinition definition = (MetaDefinition) map.getStructure(id.intValue());
						
			out2.writeObject( "uint16", new Integer(id.intValue()));
			out2.writeObject( "meta.name", name );
			out2.writeObject( "meta.definition.envelop", definition );
					
		}

		baos2.close();
		
		byte[] extBuffer = baos2.toByteArray();
		out.writeObject( "uint16", new Integer( extBuffer.length ));
		out.getStream().write( extBuffer );
	}
    
}
