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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;


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
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaDefinition;


public class MessageReader 
{		
	public static ReferenceTypeMap readMessageDataDictionary( TypeLibrary library, InputStream is)
	throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap( library, new TypeMapperCore(new TypeMapperError()));
		
		ReferenceTypeMap core = new ReferenceTypeMap( library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())), refCore);
		
		TypeInputStream tmis = new TypeInputStream( is, core );
    
		// read the core map.
		ReferenceTypeMap coreMap = readCore( library, tmis );
		
		// read the message map using the core map.        
		ReferenceTypeMap messageMap = readMessageMap( tmis, coreMap );
		return messageMap;
	}
	
	public static Object readMessage( TypeLibrary library, InputStream fis) 
	throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap( library, new TypeMapperCore(new TypeMapperError()));
		ReferenceTypeMap core = new ReferenceTypeMap( library, new TypeMapperDynamic(new TypeMapperCore(new TypeMapperError())), refCore);
		TypeInputStream tmis = new TypeInputStream( fis, core );
         
		// read the core map.
		ReferenceTypeMap coreMap = readCore( library, tmis );
		
		// read the message map using the core map.        
		ReferenceTypeMap messageMap = readMessageMap( tmis, coreMap );
		
		// read the final dictionary. register types if needed.
		tmis.setTypeMap( messageMap );
		
		Integer ident = (Integer) tmis.readObject( UInt16.TYPENAME );

		return tmis.readObject( ident.intValue() );
	}

	private static ReferenceTypeMap readCore( TypeLibrary library, TypeInputStream tmis ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(library, new TypeMapperCore(new TypeMapperError()));
		ReferenceTypeMap core = new ReferenceTypeMap( library, new TypeMapperCore(new TypeMapperError()), refCore);
		
		// read the core array size.  expect = 2.
		Short arraySize = (Short) tmis.readObject( UInt8.TYPENAME );

		Integer coreSize = (Integer) tmis.readObject( UInt16.TYPENAME );
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
			Integer extSize = (Integer) tmis.readObject( UInt16.TYPENAME );
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
			newType.id = ((Integer)dictDataIn.readObject(UVInt28.TYPENAME)).intValue();
			newType.location = (TypeLocation) dictDataIn.readObject(DictionaryLocation.TYPENAME );
			newType.structure = (byte[]) dictDataIn.readObject( MetaDefinition.META_DEFINITION_ENVELOPE );
			newTypes[x] = newType;
		}

		//TypeMap mapSpec = new TypeMap( dict.getLibrary() );
		coreMap.setReferenceMap(mapSpec);
		
		// Step 2. Reserve unknown types and map types.
		for ( int x = 0; x<size; x++ )
		{
			if (newTypes[x].location instanceof DictionaryDefinition)
			{
				DictionaryDefinition definition = (DictionaryDefinition) newTypes[x].location;
				//int nameId = library.getTypeId(definition.getName());
				DictionaryName dictName = new DictionaryName(definition.getName());
				int nameId = TypeLibrary.NOTYPE;
				if ( library.getTypeState( dictName ) == TypeLibrary.TYPE_NOT_DEFINED )
				{
					throw new TypeException("Unknown Type Encounted");
				}
				else
				{
					nameId = library.getTypeId(definition.getName());
				}
				
				definition.setId(nameId);

				int typeId = library.getTypeId(definition);
				if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_NOT_DEFINED )
				{
					throw new TypeException("Unknown Type Encounted");
				}
				else
				{
					newTypes[x].reserved = false;
					mapSpec.map( newTypes[x].id, typeId );
					
				}
			}
		}

		// Step 3. Compare known types with resolved structures.
		for ( int x = 0 ; x<size; x++ )
		{
			if (newTypes[x].location instanceof DictionaryDefinition)
			{				
				newTypes[x].definition = (MetaDefinition) TypeHelper.readStructure( coreMap, newTypes[x].structure );

				// Check if its already defined.
				int typeId = library.getTypeId(newTypes[x].location);
				if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_RESERVED )
				{	
					throw new TypeException("Should not be reachable");
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
		
		// Step 3. Read structures.
		// Don't register until all structures are read and type names
		// are resolved.
		for ( int x = 0; x<size; x++ )
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

	public static byte[] writeCore( TypeMap map ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(map.getLibrary(), new TypeMapperCore(new TypeMapperError()));
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream coreObjectStream = new TypeOutputStream( baos1, map );
		coreObjectStream.writeObject( Dictionary.DICTIONARY_ENTRY_LIST, refCore );
		baos1.close();		
		return baos1.toByteArray();
	}

	public static class Triple
	{
		public int id;
		public String version;
		public byte[] structure;
		public boolean reserved;
		public TypeLocation location;
		public MetaDefinition definition;
	}

}
