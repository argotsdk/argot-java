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
package com.argot.dictionary;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.argot.DynamicTypeMap;
import com.argot.ReferenceTypeMap;
import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeMapCore;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.meta.MetaDefinition;

/**
 * The Dictionary only has two public methods.  One to write a Dictionary, the other to read a Dictionary.
 * 
 * Most of this code is duplicated with MessageReader and MessageWriter.  This should be cleaned up to
 * use the MessageReader/Writer instead of duplicating code.
 */
public class Dictionary
{
	public static void writeDictionary(  OutputStream fos, TypeMap map ) throws TypeException, IOException 
	{
		TypeLibrary library = map.getLibrary();
		
		// write out the dictionary used to write the content.
		TypeMapCore refCore = TypeMapCore.getCoreTypeMap( library);
		refCore.map( 22, library.getId("dictionary.map"));
		refCore.map( 23, library.getId("dictionary.words"));
		refCore.map( 24, library.getId("dictionary.definition"));
		refCore.map( 25, library.getId("dictionary.entry"));	
		refCore.map( 26, library.getId("meta.envelop"));
		refCore.map( 27, library.getId("meta.definition#envelop"));
		
		// get the core type map.
		DynamicTypeMap core = new DynamicTypeMap( library, refCore );
		TypeMapCore.mapMeta( core, library );
		core.map( 22, library.getId("dictionary.map"));
		core.map( 23, library.getId("dictionary.words"));
		core.map( 24, library.getId("dictionary.definition"));
		core.map( 25, library.getId("dictionary.entry"));	
		core.map( 26, library.getId("meta.envelop"));
		core.map( 27, library.getId("meta.definition#envelop"));
	
		// create a dynamic type map.
		DynamicTypeMap dtm = new DynamicTypeMap( library, map );
		
		// write out the message content.  Definition of the core type map.
		ByteArrayOutputStream baos = new ByteArrayOutputStream(); 
		TypeOutputStream tmos = new TypeOutputStream( baos , dtm );
		tmos.writeObject( "u16", new Integer(dtm.getId( library.getId("dictionary.map"))));
		tmos.writeObject( "dictionary.map", map );
		tmos.getStream().close();
		baos.close();
		
		core.setReferenceMap( dtm );
		
		
		// problem is that in writing the dtm, new types might
		// need to be dynamically added.  Simple solution is to 
		// write it out until the size stablises.
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos2 = new TypeOutputStream( baos2 , core );
		int count = dtm.size();
		int lastCount = 0;
		
		while( count != lastCount )
		{
			lastCount = count;
			
			baos2 = new ByteArrayOutputStream(); 
			tmos2 = new TypeOutputStream( baos2 , core );
			tmos2.writeObject( "u8", new Integer( 1 ));
			tmos2.writeObject( "dictionary.words", dtm );
			tmos2.getStream().close();
			baos2.close();
			
			count = dtm.size();		
		}

		// write out the core used to write the message dictionary.
		core.setReferenceMap( core );
		ByteArrayOutputStream baos3 = new ByteArrayOutputStream(); 
		TypeOutputStream tmos3 = new TypeOutputStream( baos3 , core );
		writeCoreMap( tmos3, core );
		tmos3.getStream().close();
		baos3.close();
		
		// write out the file.
		//FileOutputStream fos = new FileOutputStream( "core.map" );
		fos.write( baos3.toByteArray() );
		fos.write( baos2.toByteArray() );
		fos.write( baos.toByteArray() );
		fos.close();
	}
	
	private static void writeCoreMap( TypeOutputStream out, ReferenceTypeMap map ) throws TypeException, IOException
	{
		// writing out the core and then the extensions.
		out.writeObject( "U8" , new Integer( 2 ));
		
		// write out the core elements.
		List coreIds = TypeMapCore.getCoreIdentifiers();
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream out1 = new TypeOutputStream( baos1, map );
		
		// write the number of entries.
		out1.writeObject( "U16", new Integer( coreIds.size() ));
		
			
		Iterator i = coreIds.iterator();	
		while (i.hasNext() )
		{
			int id = ((Integer) i.next()).intValue();	
			String name = map.getName( id );
			MetaDefinition definition = (MetaDefinition) map.getStructure(id);

			out1.writeObject( "U16", new Integer(id));
			out1.writeObject( "meta.name", name );
			out1.writeObject( "dictionary.definition", definition );
			
		}
		
		out1.getStream().close();
		baos1.close();
		
		byte[] coreBuffer = baos1.toByteArray();
		out.writeObject( "u16", new Integer( coreBuffer.length ));
		out.getStream().write( coreBuffer );

		// write out extensions.		
		ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
		TypeOutputStream out2 = new TypeOutputStream( baos2, map );


		// count the number of extensions
		int extensionCount = 0;
		i = map.getIterator();
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
		
		out2.writeObject("U16", new Integer( extensionCount ));
		
		// write out the extensions
		i = map.getIterator();
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
						
			out2.writeObject( "U16", new Integer(id.intValue()));
			out2.writeObject( "meta.name", name );
			out2.writeObject( "dictionary.definition", definition );
					
		}
		
		out2.getStream().close();
		baos2.close();
		
		byte[] extBuffer = baos2.toByteArray();
		out.writeObject( "u16", new Integer( extBuffer.length ));
		out.getStream().write( extBuffer );
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
		TypeMapCore refCore = TypeMapCore.getCoreTypeMap( library );
		refCore.map( 22, library.getId("dictionary.map"));
		refCore.map( 23, library.getId("dictionary.words"));
		refCore.map( 24, library.getId("dictionary.definition"));
		refCore.map( 25, library.getId("dictionary.entry"));	
		refCore.map( 26, library.getId("meta.envelop"));
		refCore.map( 27, library.getId("meta.definition#envelop"));
		
		TypeMapCore core = TypeMapCore.getCoreTypeMap( library, refCore);
		core.map( 22, library.getId("dictionary.map"));
		core.map( 23, library.getId("dictionary.words"));
		core.map( 24, library.getId("dictionary.definition"));
		core.map( 25, library.getId("dictionary.entry"));	
		core.map( 26, library.getId("meta.envelop"));
		core.map( 27, library.getId("meta.definition#envelop"));
    	
		TypeInputStream tmis = new TypeInputStream( fis, core );
        
        
		// read the core map.
		ReferenceTypeMap coreMap = readCore( library, tmis );
		// read the message map using the core map.
        
		ReferenceTypeMap messageMap = readMessageMap( tmis, coreMap );
		// read the final dictionary. register types if needed.

		Integer ident = (Integer) tmis.readObject( "u16" );
		if ( ident.intValue() != 1 )
			throw new TypeException( "Wrong dictionary index value" );

		tmis.setTypeMap( messageMap );
        
		TypeMap finalMap = new TypeMap( coreMap.getLibrary() );
		setMap( tmis, messageMap, finalMap );
		return finalMap;
	}

	private static ReferenceTypeMap readCore( TypeLibrary library, TypeInputStream tmis ) throws TypeException, IOException
	{
		TypeMapCore refCore = TypeMapCore.getCoreTypeMap( library );
		refCore.map( 22, library.getId("dictionary.map"));
		refCore.map( 23, library.getId("dictionary.words"));
		refCore.map( 24, library.getId("dictionary.definition"));
		refCore.map( 25, library.getId("dictionary.entry"));	
		refCore.map( 26, library.getId("meta.envelop"));
		refCore.map( 27, library.getId("meta.definition#envelop"));

		TypeMapCore core = TypeMapCore.getCoreTypeMap( library, refCore);
		core.map( 22, library.getId("dictionary.map"));
		core.map( 23, library.getId("dictionary.words"));
		core.map( 24, library.getId("dictionary.definition"));
		core.map( 25, library.getId("dictionary.entry"));	
		core.map( 26, library.getId("meta.envelop"));
		core.map( 27, library.getId("meta.definition#envelop"));
 		
		// read the core array size.  expect = 2.
		Short arraySize = (Short) tmis.readObject( "u8" );
		
		byte[] readcore = (byte[]) tmis.readObject( "dictionary.words" );
		byte[] localCore = writeCore( core );
		// compare the core read with my own core.

		if ( !Arrays.equals( readcore, localCore ) )
		{
			StringBuffer errorMsg = new StringBuffer();
			errorMsg.append("core dictionaries did not match");
			for ( int x =0; x< readcore.length; x++ )
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
			byte[] extension = (byte[]) tmis.readObject( "dictionary.words" );
			ByteArrayInputStream bais = new ByteArrayInputStream( extension );
			TypeInputStream extDataIn = new TypeInputStream( bais, core );
			
			setMap( extDataIn, core, core );
		}
		
		return core;
	}
	
	private static ReferenceTypeMap readMessageMap( TypeInputStream tmis, ReferenceTypeMap coreMap ) throws TypeException, IOException
	{
		ReferenceTypeMap mapSpec = new ReferenceTypeMap( coreMap.getLibrary(), coreMap );
		
		// read the core array size.  expect = 2.
		Short arraySize = (Short) tmis.readObject( "u8" );
		
		// now the core is confirmed we can add in the extensions.
		for ( int x=0; x < arraySize.intValue(); x++ )
		{
			byte[] dict = (byte[]) tmis.readObject( "dictionary.words" );
			ByteArrayInputStream bais = new ByteArrayInputStream( dict );
			TypeInputStream dictDataIn = new TypeInputStream( bais, coreMap );
			setMap( dictDataIn, coreMap, mapSpec );
		}
		
		return mapSpec;
	}

	// This reads the contents of the data dictionary and sets this
	// map up using the data contained.  It checks all data with the
	// internal library.
	private static TypeMap setMap( TypeInputStream dictDataIn, ReferenceTypeMap coreMap, TypeMap mapSpec )
	throws TypeException, IOException
	{
	
			
		int size = ((Integer)dictDataIn.readObject("U16" )).intValue();
		Triple newTypes[] = new Triple[size];
		
		// Step 1.  Read all the types in.
		for ( int x = 0; x<size; x++ )
		{
			Triple newType = new Triple();
			newType.id = ((Integer)dictDataIn.readObject( "U16")).intValue();
			newType.name = (String) dictDataIn.readObject( "meta.name" );
			newType.structure = (byte[]) dictDataIn.readObject( "dictionary.definition" );
			newTypes[x] = newType;
		}

		//TypeMap mapSpec = new TypeMap( dict.getLibrary() );
		
		// Step 2. Reserve unknown types and map types.
		for ( int x = 0; x<size; x++ )
		{
			if ( coreMap.getLibrary().getTypeState( newTypes[x].name ) == TypeLibrary.TYPE_NOT_DEFINED )
			{
				int res = coreMap.getLibrary().reserve( newTypes[x].name );
				newTypes[x].reserved = true;
				mapSpec.map( newTypes[x].id, res );
			}
			else
			{
				newTypes[x].reserved = false;
				mapSpec.map( newTypes[x].id, coreMap.getLibrary().getId( newTypes[x].name ) );
			}
		}

		// Step 3. Compare known types with resolved structures.
		for ( int x = 0 ; x<size; x++ )
		{
			if ( !newTypes[x].reserved )
			{
				if ( !mapSpec.isSame( mapSpec.getSystemId( newTypes[x].id ), newTypes[x].name, newTypes[x].structure, coreMap ) )
				{
					throw new TypeException( "type mismatch:" + newTypes[x].name );
					
				}
			}
		}
		
		// Step 3. Read structures.
		// Don't register until all structures are read and type names
		// are resolved.
		for ( int x = 0; x<size; x++ )
		{
			newTypes[x].definition = (MetaDefinition) mapSpec.readStructure( coreMap, newTypes[x].structure );
		}
		
		// Step 5. Register types with new structures.
		for ( int x = 0; x<size; x++ )
		{
			if ( newTypes[x].reserved )
			{
				mapSpec.getLibrary().register( newTypes[x].name, newTypes[x].definition );
			}
		}
		
		return mapSpec;
	}

	public static byte[] writeCore( TypeMap map ) throws TypeException, IOException
	{
		// write out the core elements.
		List coreIds = TypeMapCore.getCoreIdentifiers();
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream out1 = new TypeOutputStream( baos1, map );
		
		// write the number of entries.
		out1.writeObject( "U16", new Integer( coreIds.size() ));
		
			
		Iterator i = coreIds.iterator();	
		while (i.hasNext() )
		{
			int id = ((Integer) i.next()).intValue();	
			String name = map.getName( id );
			MetaDefinition definition = (MetaDefinition) map.getStructure(id);
						
			out1.writeObject( "U16", new Integer(id));
			out1.writeObject( "meta.name", name );
			out1.writeObject( "dictionary.definition", definition );
			
		}

		baos1.close();
		
		return baos1.toByteArray();
	}

	public static class Triple
	{
		public int id;
		public String name;
		public byte[] structure;
		public boolean reserved;
		public MetaDefinition definition;
	}
	
}
