/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot;

import java.util.ArrayList;
import java.util.List;

import com.argot.common.U8Utf8;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryBase;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaAbstractMap;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaAtom;
import com.argot.meta.MetaAtomAttribute;
import com.argot.meta.MetaAtomAttributeBigEndian;
import com.argot.meta.MetaAtomAttributeIEEE756;
import com.argot.meta.MetaAtomAttributeInteger;
import com.argot.meta.MetaAtomAttributeSigned;
import com.argot.meta.MetaAtomAttributeSize;
import com.argot.meta.MetaAtomAttributeUnsigned;
import com.argot.meta.MetaCluster;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaEncoding;
import com.argot.meta.MetaEnvelope;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentified;
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
		map.map( BASE_ID, library.getTypeId( new DictionaryBase() ));
		map.map( UINT8_ID, library.getDefinitionId( UInt8.TYPENAME, VERSION ) );
		map.map( UVINT28_ID, library.getDefinitionId( UVInt28.TYPENAME, VERSION ));
		map.map( META_GROUP_ID, library.getTypeId( "meta") );	
		map.map( META_ID, library.getDefinitionId( "meta.id", VERSION ));
		
		map.map( META_CLUSTER_ID, library.getDefinitionId( MetaCluster.TYPENAME, VERSION));
		map.map( ABSTRACT_MAP_ID, library.getDefinitionId( MetaAbstractMap.TYPENAME, VERSION ));
		map.map( ABSTRACT_ID, library.getDefinitionId( MetaAbstract.TYPENAME, VERSION ));
		map.map( U8UTF8_ID, library.getDefinitionId( U8Utf8.TYPENAME, VERSION ));
		map.map( NAME_ID, library.getDefinitionId( "meta.name", VERSION ));
		map.map( VERSION_ID, library.getDefinitionId("meta.version", VERSION));

		map.map( DEFINITION_ID, library.getDefinitionId( MetaDefinition.TYPENAME, VERSION));
		map.map( EXPRESSION_ID, library.getDefinitionId( MetaExpression.TYPENAME, VERSION ));
		map.map( REFERENCE_ID, library.getDefinitionId( MetaReference.TYPENAME, VERSION ) );
		map.map( TAG_ID, library.getDefinitionId( MetaTag.TYPENAME, VERSION ) );
		map.map( SEQUENCE_ID, library.getDefinitionId( MetaSequence.TYPENAME, VERSION ) );
		map.map( ARRAY_ID, library.getDefinitionId( MetaArray.TYPENAME, VERSION ) );
		map.map( ENVELOPE_ID, library.getDefinitionId( MetaEnvelope.TYPENAME, VERSION ) );
		map.map( ENCODED_ID, library.getDefinitionId( MetaEncoding.TYPENAME, VERSION ));
		map.map( IDENTIFIED_ID, library.getDefinitionId( MetaIdentified.TYPENAME, MetaIdentified.VERSION));

		map.map( META_ATOM_ID, library.getDefinitionId( MetaAtom.TYPENAME, VERSION ));
		map.map( META_ATOM_ATTRIBUTE_ID, library.getDefinitionId( MetaAtomAttribute.TYPENAME, VERSION ));
		map.map( META_ATTRIBUTE_ID, library.getTypeId( "meta.attribute" ));
		map.map( META_ATTRIBUTE_SIZE_ID, library.getDefinitionId( MetaAtomAttributeSize.TYPENAME, VERSION ));
		map.map( META_ATTRIBUTE_INTEGER_ID, library.getDefinitionId( MetaAtomAttributeInteger.TYPENAME, VERSION ));
		map.map( META_ATTRIBUTE_UNSIGNED_ID, library.getDefinitionId( MetaAtomAttributeUnsigned.TYPENAME, VERSION ));
		map.map( META_ATTRIBUTE_BIGENDIAN_ID, library.getDefinitionId( MetaAtomAttributeBigEndian.TYPENAME, VERSION ));
		map.map( META_ATTRIBUTE_SIGNED_ID, library.getDefinitionId( MetaAtomAttributeSigned.TYPENAME, MetaAtomAttributeSigned.VERSION ));
		map.map( META_ATTRIBUTE_IEEE756_ID, library.getDefinitionId( MetaAtomAttributeIEEE756.TYPENAME, MetaAtomAttributeIEEE756.VERSION)); 

		map.map( DICTIONARY_CLUSTER_ID, library.getTypeId("dictionary"));
		map.map( DICTIONARY_BASE_ID, library.getDefinitionId( DictionaryBase.TYPENAME, VERSION ));
		map.map( DICTIONARY_NAME_ID, library.getDefinitionId( DictionaryName.TYPENAME, VERSION ));
		map.map( DICTIONARY_DEFINITION_ID, library.getDefinitionId( DictionaryDefinition.TYPENAME, VERSION ));
		map.map( DICTIONARY_RELATION_ID, library.getDefinitionId( DictionaryRelation.TYPENAME, VERSION ));
		map.map( DICTIONARY_LOCATION_ID, library.getDefinitionId( DictionaryLocation.TYPENAME, VERSION ));

		map.map( DICTIONARY_DEFINITION_ENVELOPE_ID, library.getDefinitionId( MetaDefinition.META_DEFINITION_ENVELOPE, VERSION ));
		map.map( DICTIONARY_ENTRY_ID, library.getDefinitionId( Dictionary.DICTIONARY_ENTRY, VERSION ));
		map.map( DICTIONARY_ENTRY_LIST_ID, library.getDefinitionId( Dictionary.DICTIONARY_ENTRY_LIST, VERSION ));		
		map.map( DICTIONARY_FILE_ID, library.getDefinitionId( Dictionary.DICTIONARY_FILE, Dictionary.DICTIONARY_FILE_VERSION));
		
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

	
	public static List<Integer> getCoreIdentifiers()
	{
		List<Integer> coreIds = new ArrayList<Integer>();
		
		//entities
		coreIds.add( new Integer( BASE_ID ) );
		coreIds.add( new Integer( UINT8_ID ) );
		coreIds.add( new Integer( UVINT28_ID ));
		coreIds.add( new Integer( META_GROUP_ID ));
		coreIds.add( new Integer( META_ID ));
		
		
		coreIds.add( new Integer( META_CLUSTER_ID ));
		coreIds.add( new Integer( ABSTRACT_MAP_ID ));
		coreIds.add( new Integer( ABSTRACT_ID ));
		coreIds.add( new Integer( U8UTF8_ID ));
		coreIds.add( new Integer( NAME_ID ));
		coreIds.add( new Integer( VERSION_ID ));
		
		
		coreIds.add( new Integer( DEFINITION_ID ));
		coreIds.add( new Integer( EXPRESSION_ID ));
		coreIds.add( new Integer( REFERENCE_ID ));
		coreIds.add( new Integer( TAG_ID ));
		coreIds.add( new Integer( SEQUENCE_ID ));
		coreIds.add( new Integer( ARRAY_ID ));
		coreIds.add( new Integer( ENVELOPE_ID ));
		coreIds.add( new Integer( ENCODED_ID ));
		coreIds.add( new Integer( IDENTIFIED_ID ));
		
		coreIds.add( new Integer( META_ATOM_ID ));
		coreIds.add( new Integer( META_ATOM_ATTRIBUTE_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_SIZE_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_INTEGER_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_UNSIGNED_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_BIGENDIAN_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_SIGNED_ID ));
		coreIds.add( new Integer( META_ATTRIBUTE_IEEE756_ID ));
		
		coreIds.add( new Integer( DICTIONARY_CLUSTER_ID ));
		coreIds.add( new Integer( DICTIONARY_BASE_ID ));		
		coreIds.add( new Integer( DICTIONARY_NAME_ID ));
		coreIds.add( new Integer( DICTIONARY_DEFINITION_ID ));
		coreIds.add( new Integer( DICTIONARY_RELATION_ID ));
		coreIds.add( new Integer( DICTIONARY_LOCATION_ID ));
		
		coreIds.add( new Integer( DICTIONARY_DEFINITION_ENVELOPE_ID ));
		coreIds.add( new Integer( DICTIONARY_ENTRY_ID ));
		coreIds.add( new Integer( DICTIONARY_ENTRY_LIST_ID ));
		coreIds.add( new Integer( DICTIONARY_FILE_ID ));
		
		return coreIds;
	}

	public static int BASE_ID = 0;
	public static int UINT8_ID = 1;
	public static int UVINT28_ID = 2;
	public static int META_GROUP_ID = 3;
	public static int META_ID = 4;
	
	public static int META_CLUSTER_ID = 5;
	public static int ABSTRACT_MAP_ID = 6;
	public static int ABSTRACT_ID = 7;
	public static int U8UTF8_ID = 8;
	public static int NAME_ID = 9;	
	public static int VERSION_ID = 10;

	public static int DEFINITION_ID = 11;
	public static int EXPRESSION_ID = 12;
	public static int REFERENCE_ID = 13;
	public static int TAG_ID = 14;
	public static int SEQUENCE_ID = 15;
	public static int ARRAY_ID = 16;
	public static int ENVELOPE_ID = 17;
	public static int ENCODED_ID = 18;
	public static int IDENTIFIED_ID = 19;

	public static int META_ATOM_ID = 20;
	public static int META_ATOM_ATTRIBUTE_ID = 21;
	public static int META_ATTRIBUTE_ID = 22;
	public static int META_ATTRIBUTE_SIZE_ID = 23;
	public static int META_ATTRIBUTE_INTEGER_ID = 24;
	public static int META_ATTRIBUTE_UNSIGNED_ID = 25;
	public static int META_ATTRIBUTE_BIGENDIAN_ID = 26;
	public static int META_ATTRIBUTE_SIGNED_ID = 27;
	public static int META_ATTRIBUTE_IEEE756_ID = 28;
	
	public static int DICTIONARY_CLUSTER_ID = 29;
	public static int DICTIONARY_BASE_ID = 30;
	public static int DICTIONARY_NAME_ID = 31;
	public static int DICTIONARY_DEFINITION_ID = 32;
	public static int DICTIONARY_RELATION_ID = 33;
	public static int DICTIONARY_LOCATION_ID = 34;
	
	public static int DICTIONARY_DEFINITION_ENVELOPE_ID = 35;
	public static int DICTIONARY_ENTRY_ID = 36;
	public static int DICTIONARY_ENTRY_LIST_ID = 37;	
	public static int DICTIONARY_FILE_ID = 38;
	

	
	public static int META_SIZE = 39;
}
