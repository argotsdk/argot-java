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
package com.argot;

import java.util.ArrayList;
import java.util.List;

import com.argot.common.U8Ascii;
import com.argot.common.UInt8;
import com.argot.common.UInt16;
import com.argot.common.Empty;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaEncoding;
import com.argot.meta.MetaEnvelop;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaFixedWidth;
import com.argot.meta.MetaFixedWidthAttribute;
import com.argot.meta.MetaFixedWidthAttributeBigEndian;
import com.argot.meta.MetaFixedWidthAttributeInteger;
import com.argot.meta.MetaFixedWidthAttributeSigned;
import com.argot.meta.MetaFixedWidthAttributeSize;
import com.argot.meta.MetaFixedWidthAttributeUnsigned;
import com.argot.meta.MetaMap;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;

public class TypeMapCore
extends ReferenceTypeMap
{
	public static TypeMapCore _coreTypeMap;
	
	public static TypeMapCore getCoreTypeMap( TypeLibrary library, TypeMap refMap )
	throws TypeException
	{
		return new TypeMapCore(library, refMap);
	}

	public static TypeMapCore getCoreTypeMap( TypeLibrary library )
	throws TypeException
	{
	    TypeMapCore refMap = new TypeMapCore( library, null );
		refMap.setReferenceMap( refMap );
		return refMap;
	}

	private TypeMapCore( TypeLibrary library, TypeMap refMap )
	throws TypeException
	{
		super( library, refMap );
		mapMeta( this, library );
	}
	
	public static void mapMeta( TypeMap map, TypeLibrary library )
	throws TypeException
	{
		map.map( EMPTY_ID, library.getId( Empty.TYPENAME ));
		map.map( UINT8_ID, library.getId( UInt8.TYPENAME ) );
		map.map( UINT16_ID, library.getId( UInt16.TYPENAME ) );
		
		map.map( META_ID, library.getId( "meta.id" ));
		map.map( ABSTRACT_ID, library.getId( MetaAbstract.TYPENAME ));
		map.map( MAP_ID, library.getId( MetaMap.TYPENAME ));
		map.map( U8ASCII_ID, library.getId( U8Ascii.TYPENAME ));
		map.map( NAME_ID, library.getId( "meta.name" ));
		
		map.map( EXPRESSION_ID, library.getId( MetaExpression.TYPENAME ));
		map.map( REFERENCE_ID, library.getId( MetaReference.TYPENAME ) );
		map.map( TAG_ID, library.getId( MetaTag.TYPENAME ) );
		map.map( SEQUENCE_ID, library.getId( MetaSequence.TYPENAME ) );
		map.map( ARRAY_ID, library.getId( MetaArray.TYPENAME ) );
		map.map( ENVELOP_ID, library.getId( MetaEnvelop.TYPENAME ) );
		map.map( ENCODED_ID, library.getId( MetaEncoding.TYPENAME ));


		map.map( FIXED_WIDTH_ID, library.getId( MetaFixedWidth.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_ID, library.getId( MetaFixedWidthAttribute.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_SIZE_ID, library.getId( MetaFixedWidthAttributeSize.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_SIGNED_ID, library.getId( MetaFixedWidthAttributeSigned.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID, library.getId( MetaFixedWidthAttributeUnsigned.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_INTEGER_ID, library.getId( MetaFixedWidthAttributeInteger.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID, library.getId( MetaFixedWidthAttributeBigEndian.TYPENAME ));
		map.map( FIXED_WIDTH_ATTRIBUTE_TO_SIZE_ID, library.getId( "meta.fixed_width.attribute#meta.fixed_width.attribute.size" ));
		map.map( FIXED_WIDTH_ATTRIBUTE_TO_SIGNED_ID, library.getId( "meta.fixed_width.attribute#meta.fixed_width.attribute.signed" ));
		map.map( FIXED_WIDTH_ATTRIBUTE_TO_UNSIGNED_ID, library.getId( "meta.fixed_width.attribute#meta.fixed_width.attribute.unsigned" ));
		map.map( FIXED_WIDTH_ATTRIBUTE_TO_INTEGER_ID, library.getId( "meta.fixed_width.attribute#meta.fixed_width.attribute.integer" ));
		map.map( FIXED_WIDTH_ATTRIBUTE_TO_BIGENDIAN_ID, library.getId( "meta.fixed_width.attribute#meta.fixed_width.attribute.bigendian" ));


		map.map( EXPRESSION_TO_REFERENCE_ID, library.getId( "meta.expression#meta.reference"));
		map.map( EXPRESSION_TO_TAG_ID, library.getId( "meta.expression#meta.tag"));
		map.map( EXPRESSION_TO_SEQUENCE_ID, library.getId( "meta.expression#meta.sequence"));
		map.map( EXPRESSION_TO_ARRAY_ID, library.getId( "meta.expression#meta.array"));
		map.map( EXPRESSION_TO_ENVELOP_ID, library.getId( "meta.expression#meta.envelop"));
		map.map( EXPRESSION_TO_ENCODED_ID, library.getId( "meta.expression#meta.encoding"));
				
		map.map( DEFINITION_ID, library.getId( "meta.definition"));
		map.map( DEFINITION_TO_FIXED_WIDTH_ID, library.getId( "meta.definition#meta.fixed_width"));
		map.map( DEFINITION_TO_MAP_ID, library.getId( "meta.definition#meta.map"));
		map.map( DEFINITION_TO_ABSTRACT_ID, library.getId( "meta.definition#meta.abstract"));
		map.map( DEFINITION_TO_EXPRESSION_ID, library.getId( "meta.definition#meta.expression"));				

		map.map( DEFINITION_ENVELOP_ID, library.getId( "meta.definition.envelop"));
		map.map( DICTIONARY_ENTRY_ID, library.getId( "dictionary.entry"));
		map.map( DICTIONARY_ENTRY_LIST_ID, library.getId( "dictionary.entry.list"));		
	}
	
	public static List getCoreIdentifiers()
	{
		List coreIds = new ArrayList();
		
		coreIds.add( new Integer( EMPTY_ID ) );
		coreIds.add( new Integer( UINT8_ID ) );
		coreIds.add( new Integer( UINT16_ID ));
		
		coreIds.add( new Integer( META_ID ));
		coreIds.add( new Integer( ABSTRACT_ID ));
		coreIds.add( new Integer( MAP_ID ));
		coreIds.add( new Integer( U8ASCII_ID ));
		coreIds.add( new Integer( NAME_ID ));
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
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_SIGNED_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_INTEGER_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_TO_SIZE_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_TO_SIGNED_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_TO_UNSIGNED_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_TO_INTEGER_ID ));
		coreIds.add( new Integer( FIXED_WIDTH_ATTRIBUTE_TO_BIGENDIAN_ID ));

		coreIds.add( new Integer( EXPRESSION_TO_REFERENCE_ID ));
		coreIds.add( new Integer( EXPRESSION_TO_TAG_ID ));
		coreIds.add( new Integer( EXPRESSION_TO_SEQUENCE_ID ));
		coreIds.add( new Integer( EXPRESSION_TO_ARRAY_ID ));
		coreIds.add( new Integer( EXPRESSION_TO_ENVELOP_ID ));
		coreIds.add( new Integer( EXPRESSION_TO_ENCODED_ID ));

		coreIds.add( new Integer( DEFINITION_ID ));
		coreIds.add( new Integer( DEFINITION_TO_FIXED_WIDTH_ID ));
		coreIds.add( new Integer( DEFINITION_TO_MAP_ID ));
		coreIds.add( new Integer( DEFINITION_TO_ABSTRACT_ID ));	
		coreIds.add( new Integer( DEFINITION_TO_EXPRESSION_ID ));
		
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
	public static int MAP_ID = 6;
	public static int U8ASCII_ID = 7;
	public static int NAME_ID = 8;	
	public static int EXPRESSION_ID = 9;
	public static int REFERENCE_ID = 10;
	public static int TAG_ID = 11;
	public static int SEQUENCE_ID = 12;
	public static int ARRAY_ID = 13;
	public static int ENVELOP_ID = 14;
	public static int ENCODED_ID = 15;

	public static int FIXED_WIDTH_ID = 16;
	public static int FIXED_WIDTH_ATTRIBUTE_ID = 17;
	public static int FIXED_WIDTH_ATTRIBUTE_SIZE_ID = 18;
	public static int FIXED_WIDTH_ATTRIBUTE_SIGNED_ID = 19;
	public static int FIXED_WIDTH_ATTRIBUTE_UNSIGNED_ID = 20;
	public static int FIXED_WIDTH_ATTRIBUTE_INTEGER_ID = 21;
	public static int FIXED_WIDTH_ATTRIBUTE_BIGENDIAN_ID = 22;
	public static int FIXED_WIDTH_ATTRIBUTE_TO_SIZE_ID = 23;
	public static int FIXED_WIDTH_ATTRIBUTE_TO_SIGNED_ID = 24;
	public static int FIXED_WIDTH_ATTRIBUTE_TO_UNSIGNED_ID = 25;
	public static int FIXED_WIDTH_ATTRIBUTE_TO_INTEGER_ID = 26;
	public static int FIXED_WIDTH_ATTRIBUTE_TO_BIGENDIAN_ID = 27;

	public static int EXPRESSION_TO_REFERENCE_ID = 28;
	public static int EXPRESSION_TO_TAG_ID = 29;
	public static int EXPRESSION_TO_SEQUENCE_ID = 30;
	public static int EXPRESSION_TO_ARRAY_ID = 31;
	public static int EXPRESSION_TO_ENVELOP_ID = 32;
	public static int EXPRESSION_TO_ENCODED_ID = 33;
	
	public static int DEFINITION_ID = 34;
	public static int DEFINITION_TO_FIXED_WIDTH_ID = 35;
	public static int DEFINITION_TO_MAP_ID = 36;
	public static int DEFINITION_TO_ABSTRACT_ID = 37;
	public static int DEFINITION_TO_EXPRESSION_ID = 38;

	public static int DEFINITION_ENVELOP_ID = 39;
	public static int DICTIONARY_ENTRY_ID = 40;
	public static int DICTIONARY_ENTRY_LIST_ID = 41;
}
