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
package com.argot.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.argot.ReferenceTypeMap;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaIdentity;

/**
 * The Dictionary only has two public methods.  One to write a Dictionary, the other to read a Dictionary.
 * 
 * Most of this code is duplicated with MessageReader and MessageWriter.  This should be cleaned up to
 * use the MessageReader/Writer instead of duplicating code.
 */
public class Dictionary
{
	public static final String DICTIONARY_ENTRY = "dictionary.entry";
	public static final String DICTIONARY_ENTRY_VERSION = "1.3";
	
	public static final String DICTIONARY_ENTRY_LIST = "dictionary.entry.list";
	public static final String DICTIONARY_ENTRY_LIST_VERSION = "1.3";
		
	public static void writeDictionary(  OutputStream fos, TypeMap map ) throws TypeException, IOException 
	{
		TypeLibrary library = map.getLibrary();
		
		// write out the dictionary used to write the content.
		ReferenceTypeMap refCore = new ReferenceTypeMap( library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())));

		// get the core type map.
		ReferenceTypeMap core = new ReferenceTypeMap( library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())), refCore);

		// create a dynamic type map.	
		ReferenceTypeMap dynamicDictionaryMap = new ReferenceTypeMap( library, new TypeMapperDynamic(new TypeMapperError()), map);

		ByteArrayOutputStream messageData = new ByteArrayOutputStream();
		int count = map.size();
		int lastCount = 0;

		// write out the message content.  Definition of the core type map.
		while( count != lastCount )
		{
			lastCount = count;
			messageData = new ByteArrayOutputStream(); 
			TypeOutputStream messageDataStream = new TypeOutputStream( messageData , dynamicDictionaryMap );
			messageDataStream.writeObject( UVInt28.TYPENAME, new Integer(dynamicDictionaryMap.getStreamId(DICTIONARY_ENTRY_LIST)));
			messageDataStream.writeObject( DICTIONARY_ENTRY_LIST, map );
			messageData.close();
			
			count = map.size();
		}
		// problem is that in writing the dtm, new types might
		// need to be dynamically added.  Simple solution is to 
		// write it out until the size stablises.
		// Going Backwards, this writes out the data dictionary.
		core.setReferenceMap( dynamicDictionaryMap );
		ByteArrayOutputStream dictionaryStream = new ByteArrayOutputStream(); 
		TypeOutputStream dictionaryObjectStream = new TypeOutputStream( dictionaryStream , core );
		count = dynamicDictionaryMap.size();
		lastCount = 0;
		
		dynamicDictionaryMap.setReferenceMap(dynamicDictionaryMap);
		while( count != lastCount )
		{
			lastCount = count;
			
			dictionaryStream = new ByteArrayOutputStream(); 
			dictionaryObjectStream = new TypeOutputStream( dictionaryStream , dynamicDictionaryMap );
			dictionaryObjectStream.writeObject( UInt8.TYPENAME, new Integer( 1 ));
			dictionaryObjectStream.writeObject( DICTIONARY_ENTRY_LIST, dynamicDictionaryMap );
			dictionaryObjectStream.getStream().close();
			dictionaryStream.close();
			
			count = dynamicDictionaryMap.size();		
		}

		dictionaryStream = new ByteArrayOutputStream(); 
		dictionaryObjectStream = new TypeOutputStream( dictionaryStream , core );
		dictionaryObjectStream.writeObject( UInt8.TYPENAME, new Integer( 1 ));
		dictionaryObjectStream.writeObject( DICTIONARY_ENTRY_LIST, dynamicDictionaryMap );
		dictionaryObjectStream.getStream().close();
		dictionaryStream.close();
		
		
		// write out the core meta dictionary used to write the message dictionary.
		core.setReferenceMap( core );
		ByteArrayOutputStream baos3 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos3 = new TypeOutputStream( baos3 , core );
		writeCoreMap( tmos3, core );
		tmos3.getStream().close();
		baos3.close();
	
		// write out the file.
		fos.write( baos3.toByteArray() );
		fos.write( dictionaryStream.toByteArray() );
		fos.write( messageData.toByteArray() );
		fos.close();
	}
	
/*
 * 
 *   (entry 
 *   	(location name:"dictionary")
 *   	(meta.sequence [
 *   		(meta.array
 *   			(reference #uint8)
 *   			(meta.envelop
 *   				(reference #uint16)
 *   				(reference #dictionary.entry_list)
 *   	] ))
 * 
 * 
 * 
 * 
 */
	
	private static void writeCoreMap( TypeOutputStream out, ReferenceTypeMap map ) throws TypeException, IOException
	{
		// writing out the core and then the extensions.
		out.writeObject( UInt8.TYPENAME , new Integer( 2 ));
		
		byte[] coreBuffer = writeCore( map );
		out.writeObject( UVInt28.TYPENAME, new Integer( coreBuffer.length ));
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
			
			out2.writeObject(UVInt28.TYPENAME, new Integer( extensionCount ));

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

				out2.writeObject( UVInt28.TYPENAME, new Integer(id.intValue()));
				out2.writeObject( DictionaryLocation.TYPENAME, location);
				out2.writeObject( MetaDefinition.META_DEFINITION_ENVELOPE, definition );
			}
			
			out2.getStream().close();
			baos2.close();
			
			count = map.size();
		}
		
		byte[] extBuffer = baos2.toByteArray();
		out.writeObject( UVInt28.TYPENAME, new Integer( extBuffer.length ));
		out.getStream().write( extBuffer );
	}
	
	
	public static byte[] writeCore( TypeMap map ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(map.getLibrary(), new TypeMapperCore(new TypeMapperError()));
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream coreObjectStream = new TypeOutputStream( baos1, map );
		coreObjectStream.writeObject( DICTIONARY_ENTRY_LIST, refCore );
		baos1.close();		
		return baos1.toByteArray();
	}
	
	
	/**
	 * This is the read dictionary section.  All methods below only relate to this function.
	 * 
	 * @param fis
	 * @throws TypeException
	 * @throws IOException
	 */
	public static TypeMap readDictionary( TypeLibrary library, InputStream fis) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(library, new TypeMapperCore(new TypeMapperError()));
		ReferenceTypeMap core = new ReferenceTypeMap(library, new TypeMapperCore(new TypeMapperError()), refCore);		
		TypeInputStream tmis = new TypeInputStream( fis, core );      
        
		// read the core map.
		ReferenceTypeMap coreMap = readCore( library, tmis );
		// read the message map using the core map.   
		
		ReferenceTypeMap messageMap = readMessageMap( tmis, coreMap );
		// read the final dictionary. register types if needed.

		Integer ident = (Integer) tmis.readObject( UVInt28.TYPENAME );
		if ( ident.intValue() != messageMap.getStreamId(DICTIONARY_ENTRY_LIST) )
			throw new TypeException( "Wrong dictionary index value: " + ident.intValue() );

		tmis.setTypeMap( messageMap );
        
		TypeMap finalMap = new TypeMap( coreMap.getLibrary(), new TypeMapperError() );
		setMap( tmis, messageMap, finalMap );
		return finalMap;
	}

	private static ReferenceTypeMap readCore( TypeLibrary library, TypeInputStream tmis ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(library, new TypeMapperCore(new TypeMapperError()));
		ReferenceTypeMap core = new ReferenceTypeMap( library, new TypeMapperCore(new TypeMapperError()), refCore);
		
		// read the core array size.  expect = 2.
		Short arraySize = (Short) tmis.readObject( UInt8.TYPENAME );
		Integer coreSize = (Integer) tmis.readObject( UVInt28.TYPENAME );
		byte[] readcore = new byte[coreSize.intValue()];		
		tmis.getStream().read(readcore,0,coreSize.intValue());

		byte[] localCore = writeCore( core );

		// compare the core read with my own core.
		if ( !Arrays.equals( readcore, localCore ) )
		{
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("core dictionaries did not match");
			for ( int x =0; x< readcore.length && x<localCore.length ; x++ )
			{
				if ( readcore[x] != localCore[x] )
				{
					errorMsg.append("no match at: " + x + "," + readcore[x] + "," + localCore[x]+"\n" );
				}
			}
			throw new TypeException(errorMsg.toString());
		}
		
		// now the core is confirmed we can add in the extensions.
		for ( int x=0; x < arraySize.intValue()-1; x++ )
		{
			Integer extSize = (Integer) tmis.readObject( UVInt28.TYPENAME );
			byte[] extension = new byte[extSize.intValue()];
			tmis.getStream().read(extension,0,extSize.intValue());
			
			ByteArrayInputStream bais = new ByteArrayInputStream( extension );
			TypeInputStream extDataIn = new TypeInputStream( bais, core );
			
			setMap( extDataIn, core, core );
		}
		
		return core;
	}
	
	private static ReferenceTypeMap readMessageMap( TypeInputStream tmis, ReferenceTypeMap coreMap ) throws TypeException, IOException
	{
		ReferenceTypeMap mapSpec = new ReferenceTypeMap( coreMap.getLibrary(), new TypeMapperError(), coreMap );
		
		// read the data dictionary array size.  expect = 1.
		Short arraySize = (Short) tmis.readObject( UInt8.TYPENAME );
		
		// now the core is confirmed we can add in the extensions.
		for ( int x=0; x < arraySize.intValue(); x++ )
		{
			setMap( tmis, coreMap, mapSpec );
		}
		
		return mapSpec;
	}

	// This reads the contents of the data dictionary and sets this
	// map up using the data contained.  It checks all data with the
	// internal library.
	private static TypeMap setMap( TypeInputStream dictDataIn, ReferenceTypeMap coreMap, TypeMap mapSpec )
	throws TypeException, IOException
	{
		TypeLibrary library = mapSpec.getLibrary();
			
		int size = ((Integer)dictDataIn.readObject( UVInt28.TYPENAME )).intValue();
		Triple newTypes[] = new Triple[size];
		
		// Step 1.  Read all the types in.
		for ( int x = 0; x<size; x++ )
		{
			Triple newType = new Triple();
			newType.id = ((Integer)dictDataIn.readObject( UVInt28.TYPENAME )).intValue();
			newType.location = (TypeLocation) dictDataIn.readObject(DictionaryLocation.TYPENAME);
			newType.structure = (byte[]) dictDataIn.readObject( MetaDefinition.META_DEFINITION_ENVELOPE );
			newTypes[x] = newType;
		}
		
		coreMap.setReferenceMap(mapSpec);		
		
		// Reserve and map all the named types.
		for (int x= 0; x<size; x++)
		{
			if (newTypes[x].location instanceof DictionaryDefinition)
			{
				DictionaryDefinition definition = (DictionaryDefinition) newTypes[x].location;
				DictionaryName dictName = new DictionaryName(definition.getName());
				int nameId = TypeLibrary.NOTYPE;
				if ( library.getTypeState( dictName ) == TypeLibrary.TYPE_NOT_DEFINED )
				{
					nameId = library.register(new DictionaryName(definition.getName()), new MetaIdentity() );
				}
				else
				{
					nameId = library.getTypeId(definition.getName());
				}
				
				definition.setId(nameId);

				int typeId = library.getTypeId(definition);
				if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_NOT_DEFINED )
				{
					int nt = library.reserve(definition);
					newTypes[x].reserved = true;
					mapSpec.map( newTypes[x].id, nt );
				}
				else
				{
					newTypes[x].reserved = false;
					mapSpec.map( newTypes[x].id, typeId );
					
				}
			}

		}
		
		// With the names registered all dictionary definitions should be able to be read
		for (int x= 0; x<size; x++)
		{
			if (newTypes[x].location instanceof DictionaryDefinition)
			{				
				DictionaryDefinition dd = (DictionaryDefinition) newTypes[x].location;
				
				// The target needs to be mapped back to the library identifier.
				//dd.setId(mapSpec.getDefinitionId(dd.getId()));

				newTypes[x].definition = (MetaDefinition) TypeHelper.readStructure( coreMap, newTypes[x].structure );

				// Check if its already defined.
				int typeId = library.getTypeId(newTypes[x].location);
				if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_RESERVED )
				{	
					int nt = library.bind( typeId, newTypes[x].definition );
					
					//int res = library.reserve( newTypes[x].location );
					//newTypes[x].reserved = true;
					mapSpec.map( newTypes[x].id, nt );
				}
				else
				{
					mapSpec.map( newTypes[x].id, typeId );

					try 
					{
						TypeHelper.isSame( mapSpec.getDefinitionId( newTypes[x].id ), newTypes[x].location, newTypes[x].structure, coreMap );					
					} 
					catch ( TypeException ex )
					{
						throw new TypeException( "type mismatch:", ex );
					}
				}
			}
		}
		
		// Names and definitions defined.  Now add in the relations.
		for (int x=0; x<size; x++)
		{
			if (newTypes[x].location instanceof DictionaryRelation)
			{
				DictionaryRelation dd = (DictionaryRelation) newTypes[x].location;
				// map target definition to internal library identifier.
				dd.setId(mapSpec.getDefinitionId(dd.getId()));
				
				newTypes[x].definition = (MetaDefinition) TypeHelper.readStructure( coreMap, newTypes[x].structure );
				
				int typeId = library.getTypeId(newTypes[x].location);
				if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_NOT_DEFINED )
				{
					int nt = library.register( newTypes[x].location, newTypes[x].definition );
					mapSpec.map( newTypes[x].id, nt );
				}
				else
				{
					mapSpec.map( newTypes[x].id, typeId );

					try 
					{
						TypeHelper.isSame( mapSpec.getDefinitionId( newTypes[x].id ), newTypes[x].location, newTypes[x].structure, coreMap );					
					} 
					catch ( TypeException ex )
					{
						throw new TypeException( "type mismatch:", ex );
					}						
				}
			}
		}

		return mapSpec;
	}

	
	public static void writeByteData( byte[] data )
	{
		int byteCount = 0;
		System.err.print("\n" + data.length +": ");
		for (int x=0; x<data.length; x++)
		{
			System.err.print("" + data[x] + " ");
			if (byteCount++ > 20)
			{
				System.err.println("");
				byteCount = 0;
			}
		}

	}

	public static class Triple
	{
		public int id;
		public byte[] structure;
		public boolean reserved;
		public TypeLocation location;
		public MetaDefinition definition;
	}
	
}
