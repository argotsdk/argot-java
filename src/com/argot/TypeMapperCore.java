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
package com.argot;

import java.util.ArrayList;

import java.util.List;
import com.argot.common.Empty;
import com.argot.common.U8Utf8;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaEncoding;
import com.argot.meta.MetaEnvelop;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaFixedWidth;
import com.argot.meta.MetaFixedWidthAttribute;
import com.argot.meta.MetaFixedWidthAttributeBigEndian;
import com.argot.meta.MetaFixedWidthAttributeInteger;
import com.argot.meta.MetaFixedWidthAttributeSize;
import com.argot.meta.MetaFixedWidthAttributeUnsigned;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class TypeMapperCore 
implements TypeMapper
{
	public static final String VERSION = "1.3";
	
	private TypeMapper _chain;
	
	public TypeMapperCore( TypeMapper chain )
	{
		_chain = chain;
	}
	
	public void initialise(TypeMap map) 
	throws TypeException 
	{
		TypeLibrary library = map.getLibrary();

		// Map Definitions
		map.map( EMPTY_ID, library.getDefinitionId( Empty.TYPENAME, VERSION ));
		map.map( UINT8_ID, library.getDefinitionId( UInt8.TYPENAME, VERSION ) );
		map.map( UINT16_ID, library.getDefinitionId( UInt16.TYPENAME, VERSION ) );
		
		map.map( META_ID, library.getDefinitionId( "meta.id", VERSION ));
		map.map( ABSTRACT_ID, library.getDefinitionId( MetaAbstract.TYPENAME, VERSION ));
		map.map( ABSTRACT_MAP_ID, library.getDefinitionId( MetaAbstractMap.TYPENAME, VERSION ));
		map.map( U8UTF8_ID, library.getDefinitionId( U8Utf8.TYPENAME, VERSION ));
		map.map( NAME_PART_ID, library.getDefinitionId( "meta.name_part", VERSION ));
		map.map( NAME_ID, library.getDefinitionId( "meta.name", VERSION ));
		map.map( VERSION_ID, library.getDefinitionId("meta.version", VERSION));
		map.map( DEFINITION_ID, library.getDefinitionId( MetaDefinition.TYPENAME, VERSION));
		map.map( IDENTITY_ID, library.getDefinitionId(MetaIdentity.TYPENAME, VERSION));
		
		map.map( EXPRESSION_ID, library.getDefinitionId( MetaExpression.TYPENAME, VERSION ));
		map.map( REFERENCE_ID, library.getDefinitionId( MetaReference.TYPENAME, VERSION ) );
		map.map( TAG_ID, library.getDefinitionId( MetaTag.TYPENAME, VERSION ) );
		map.map( SEQUENCE_ID, library.getDefinitionId( MetaSequence.TYPENAME, VERSION ) );
		map.map( ARRAY_ID, library.getDefinitionId( MetaArray.TYPENAME, VERSION ) );
		map.map( ENVELOP_ID, library.getDefinitionId( MetaEnvelop.TYPENAME, VERSION ) );
		map.map( ENCODED_ID, library.getDefinitionId( MetaEncoding.TYPENAME, VERSION ));

		map.map( FIXED_WIDTH_ID, library.getDefinitionId( MetaFixedWidth.TYPENAME, VERSION ));
		map.map( FIXED_WIDTH_ATTRIBUTE_ID, library.getDefinitionId( MetaFixedWidthAttribute.TYPENAME, VERSION ));
		map.map( FIXED_WIDTH_ATTRIBUTE_SIZE_ID, library.getDefinitionId( MetaFixedWidthAttributeSize.TYPENAME, VERSION ));
		map.map( FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID, library.getDefinitionId( MetaFixedWidthAttributeUnsigned.TYPENAME, VERSION ));
		map.map( FIXED_WIDTH_ATTRIBUTE_INTEGER_ID, library.getDefinitionId( MetaFixedWidthAttributeInteger.TYPENAME, VERSION ));
		map.map( FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID, library.getDefinitionId( MetaFixedWidthAttributeBigEndian.TYPENAME, VERSION ));

		map.map( DICTIONARY_NAME_ID, library.getDefinitionId( DictionaryName.TYPENAME, VERSION ));
		map.map( DICTIONARY_DEFINITION_ID, library.getDefinitionId( DictionaryDefinition.TYPENAME, VERSION ));
		map.map( DICTIONARY_RELATION_ID, library.getDefinitionId( DictionaryRelation.TYPENAME, VERSION ));
		map.map( DICTIONARY_LOCATION_ID, library.getDefinitionId( DictionaryLocation.TYPENAME, VERSION ));
			
		map.map( DEFINITION_ENVELOP_ID, library.getDefinitionId( MetaDefinition.META_DEFINITION_ENVELOP, VERSION ));
		map.map( DICTIONARY_ENTRY_ID, library.getDefinitionId( Dictionary.DICTIONARY_ENTRY, VERSION ));
		map.map( DICTIONARY_ENTRY_LIST_ID, library.getDefinitionId( Dictionary.DICTIONARY_ENTRY_LIST, VERSION ));		

		_chain.initialise(map);
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		return _chain.map(definitionId);
	}

	public int mapReverse(int id) 
	throws TypeException 
	{
		return _chain.mapReverse(id);
	}	

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		return _chain.mapDefault(nameId);
	}

	
	public static List getCoreIdentifiers()
	{
		List coreIds = new ArrayList();
		
		//entities
		coreIds.add( new Integer( EMPTY_ID ) );
		coreIds.add( new Integer( UINT8_ID ) );
		coreIds.add( new Integer( UINT16_ID ));
		
		coreIds.add( new Integer( META_ID ));
		coreIds.add( new Integer( ABSTRACT_ID ));
		coreIds.add( new Integer( ABSTRACT_MAP_ID ));
		coreIds.add( new Integer( U8UTF8_ID ));
		coreIds.add( new Integer( NAME_PART_ID ));
		coreIds.add( new Integer( NAME_ID ));
		coreIds.add( new Integer( VERSION_ID ));
		coreIds.add( new Integer( DEFINITION_ID ));
		coreIds.add( new Integer( IDENTITY_ID ));
		coreIds.add( new Integer( EXPRESSION_ID ));
		coreIds.add( new Integer( REFERENCE_ID ));
		coreIds.add( new Integer( TAG_ID ));
		coreIds.add( new Integer( SEQUENCE_ID ));
		coreIds.add( new Integer( ARRAY_ID ));
		coreIds.add( new Integer( ENVELOP_ID ));
		coreIds.add( new Integer( ENCODED_ID ));
		
		coreIds.add( new Integer( FIXED_WIDTH_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_SIZE_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_INTEGER_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID ));
		
		coreIds.add( new Integer( DICTIONARY_NAME_ID ));
		coreIds.add( new Integer( DICTIONARY_DEFINITION_ID ));
		coreIds.add( new Integer( DICTIONARY_RELATION_ID ));
		coreIds.add( new Integer( DICTIONARY_LOCATION_ID ));
		
		coreIds.add( new Integer( DEFINITION_ENVELOP_ID ));
		coreIds.add( new Integer( DICTIONARY_ENTRY_ID ));
		coreIds.add( new Integer( DICTIONARY_ENTRY_LIST_ID ));

		return coreIds;
	}

	public static int EMPTY_ID = 1;
	public static int UINT8_ID = 2;
	public static int UINT16_ID = 3;
	
	public static int META_ID = 4;
	public static int ABSTRACT_ID = 5;
	public static int ABSTRACT_MAP_ID = 6;
	public static int U8UTF8_ID = 7;
	public static int NAME_PART_ID = 8;
	public static int NAME_ID = 9;	
	public static int VERSION_ID = 10;
	public static int DEFINITION_ID = 11;
	public static int IDENTITY_ID = 12;
	public static int EXPRESSION_ID = 13;
	public static int REFERENCE_ID = 14;
	public static int TAG_ID = 15;
	public static int SEQUENCE_ID = 16;
	public static int ARRAY_ID = 17;
	public static int ENVELOP_ID = 18;
	public static int ENCODED_ID = 19;

	public static int FIXED_WIDTH_ID = 20;
	public static int FIXED_WIDTH_ATTRIBUTE_ID = 21;
	public static int FIXED_WIDTH_ATTRIBUTE_SIZE_ID = 22;
	public static int FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID = 23;
	public static int FIXED_WIDTH_ATTRIBUTE_INTEGER_ID = 24;
	public static int FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID = 25;

	public static int DICTIONARY_NAME_ID = 26;
	public static int DICTIONARY_DEFINITION_ID = 27;
	public static int DICTIONARY_RELATION_ID = 28;
	public static int DICTIONARY_LOCATION_ID = 29;
	
	public static int DEFINITION_ENVELOP_ID = 30;
	public static int DICTIONARY_ENTRY_ID = 31;
	public static int DICTIONARY_ENTRY_LIST_ID = 32;	
	
	public static int META_SIZE = 32;
}
