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
package com.argot.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.argot.ReferenceTypeMap;
import com.argot.TypeException;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.MetaDefinition;

public class MessageWriter
{	
    private TypeLibrary _library;
    
    public MessageWriter( TypeLibrary library )
    {
        _library = library;
    }
    
    public void writeMessage( OutputStream out, int id, Object object) throws TypeException, IOException
    {
		
		// write out the dictionary used to write the content.
		ReferenceTypeMap refCore = new ReferenceTypeMap( _library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));

		// write out the dictionary used to write the content.
		ReferenceTypeMap core = new ReferenceTypeMap( _library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())), refCore);
	
		// create a dynamic type map.
		ReferenceTypeMap dtm = new ReferenceTypeMap( _library, new TypeMapperDynamic( new TypeMapperError() ));
		
		// get the id of the object on the stream.
		int streamId = dtm.getStreamId(id);
		
		// write out the message content.  Definition of the core type map.
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		TypeOutputStream tmos = new TypeOutputStream( baos , dtm );
		tmos.writeObject( UInt16.TYPENAME, new Integer( streamId));
		tmos.writeObject( streamId, object );
		baos.close();
		
		// problem is that in writing the dtm, new types might
		// need to be dynamically added.  Simple solution is to 
		// write it twice.

		core.setReferenceMap( dtm );
		ByteArrayOutputStream dictionaryStream = new ByteArrayOutputStream(); 
		TypeOutputStream dictionaryObjectStream = new TypeOutputStream( dictionaryStream , core );
		int count = dtm.size();
		int lastCount = 0;
		
		dtm.setReferenceMap(dtm);
		while( count != lastCount )
		{
			lastCount = count;
			
			dictionaryStream = new ByteArrayOutputStream(); 
			dictionaryObjectStream = new TypeOutputStream( dictionaryStream , dtm );
			dictionaryObjectStream.writeObject( UInt8.TYPENAME, new Integer( 1 ));
			dictionaryObjectStream.writeObject( Dictionary.DICTIONARY_ENTRY_LIST, dtm );
			dictionaryObjectStream.getStream().close();
			dictionaryStream.close();
			
			count = dtm.size();		
		}
		
		dictionaryStream = new ByteArrayOutputStream(); 
		dictionaryObjectStream = new TypeOutputStream( dictionaryStream , core );
		dictionaryObjectStream.writeObject( UInt8.TYPENAME, new Integer( 1 ));
		dictionaryObjectStream.writeObject( Dictionary.DICTIONARY_ENTRY_LIST, dtm );
		dictionaryObjectStream.getStream().close();
		dictionaryStream.close();
		
		
		
		// write out the core used to write the message dictionary.
		core.setReferenceMap( refCore );
		ByteArrayOutputStream baos3 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos3 = new TypeOutputStream( baos3 , core );
		writeCoreMap( tmos3, core );
		baos3.close();		
		
		// write out the file.
		out.write( baos3.toByteArray() );
		out.write( dictionaryStream.toByteArray() );
		out.write( baos.toByteArray() );      
    }
    
	private static void writeCoreMap( TypeOutputStream out, ReferenceTypeMap map ) throws TypeException, IOException
	{
		// writing out the core and then the extensions.
		out.writeObject( UInt8.TYPENAME , new Integer( 2 ));
		
		byte[] coreBuffer = writeCore( map );
		out.writeObject( UInt16.TYPENAME, new Integer( coreBuffer.length ));
		out.getStream().write( coreBuffer );

		int count = map.size();
		int lastCount = -1;
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();

		while ( count != lastCount )
		{
			lastCount = count;

			// count the number of extensions
			List coreIds = TypeMapperCore.getCoreIdentifiers();
			Iterator i = coreIds.iterator();	
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
	
			// write out extensions.
			baos2 = new ByteArrayOutputStream();
	
			
			TypeOutputStream out2 = new TypeOutputStream( baos2, map );
			
			out2.writeObject(UInt16.TYPENAME, new Integer( extensionCount ));
			
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
				TypeLocation location = map.getLocation(id.intValue());
				MetaDefinition definition = (MetaDefinition) map.getStructure(id.intValue());
				out2.writeObject( UInt16.TYPENAME, new Integer(id.intValue()));
				out2.writeObject( DictionaryLocation.TYPENAME, location);
				out2.writeObject( "dictionary.definition_envelope", definition );
			}
			
			out2.getStream().close();
			baos2.close();
			
			count = map.size();
		}
		
		byte[] extBuffer = baos2.toByteArray();
		out.writeObject( UInt16.TYPENAME, new Integer( extBuffer.length ));
		out.getStream().write( extBuffer );
	}

	public static byte[] writeCore( TypeMap map ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(map.getLibrary(), new TypeMapperCore(new TypeMapperError()));
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream coreObjectStream = new TypeOutputStream( baos1, map );
		coreObjectStream.writeObject( Dictionary.DICTIONARY_ENTRY_LIST, refCore );
		baos1.close();		
		return baos1.toByteArray();
	}

	
}
