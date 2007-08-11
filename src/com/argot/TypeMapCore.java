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

import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.BigEndianUnsignedShort;
import com.argot.common.Empty;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaBasic;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;

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
		map.map( EMPTYID, library.getId( Empty.TYPENAME ));
		map.map( U8BID, library.getId( BigEndianUnsignedByte.TYPENAME ) );
		map.map( U16BID, library.getId( BigEndianUnsignedShort.TYPENAME ) );
		map.map( BASICID, library.getId( MetaBasic.TYPENAME ));
		map.map( ABSTRACTID, library.getId( "meta.abstract" ));
		map.map( MAP_ID, library.getId( "meta.map"));
		map.map( EXPRESSIONID, library.getId("meta.expression"));
		map.map( SEQUENCEID, library.getId( MetaSequence.TYPENAME ) );
		map.map( REFERENCEID, library.getId( MetaReference.TYPENAME ) );
		map.map( NAMEID, library.getId( "meta.name"));
		map.map( ENCODEDID, library.getId("meta.encoding"));
		map.map( ARRAYID, library.getId( MetaArray.TYPENAME ) );
		map.map( EXPRESSION_REFERENCE_ID, library.getId( "meta.expression#reference"));
		map.map( EXPRESSION_SEQUENCE_ID, library.getId( "meta.expression#sequence"));
		map.map( EXPRESSION_ARRAY_ID, library.getId( "meta.expression#array"));
		map.map( EXPRESSION_ENCODED_ID, library.getId( "meta.expression#encoding"));
		map.map( DEFINITION_ID, library.getId( "meta.definition"));
		map.map( DEFINITION_BASIC_ID, library.getId( "meta.definition#basic"));
		map.map( DEFINITION_MAP_ID, library.getId( "meta.definition#map"));
		map.map( DEFINITION_SEQUENCE_ID, library.getId( "meta.definition#sequence"));				
		map.map( DEFINITION_ABSTRACT_ID, library.getId( "meta.definition#abstract"));		
	}
	
	public static List getCoreIdentifiers()
	{
		List coreIds = new ArrayList();
		
		coreIds.add( new Integer(EMPTYID) );
		coreIds.add( new Integer( U8BID ) );
		coreIds.add( new Integer( U16BID ));
		coreIds.add( new Integer( BASICID ));
		coreIds.add( new Integer( ABSTRACTID ));
		coreIds.add( new Integer( MAP_ID ));
		coreIds.add( new Integer( EXPRESSIONID ));
		coreIds.add( new Integer( SEQUENCEID ));
		coreIds.add( new Integer( REFERENCEID ));
		coreIds.add( new Integer( NAMEID ));
		coreIds.add( new Integer( ENCODEDID ));
		coreIds.add( new Integer( ARRAYID ));
		coreIds.add( new Integer( EXPRESSION_REFERENCE_ID ));
		coreIds.add( new Integer( EXPRESSION_SEQUENCE_ID ));
		coreIds.add( new Integer( EXPRESSION_ARRAY_ID ));
		coreIds.add( new Integer( EXPRESSION_ENCODED_ID ));
		coreIds.add( new Integer( DEFINITION_ID ));
		coreIds.add( new Integer( DEFINITION_BASIC_ID ));
		coreIds.add( new Integer( DEFINITION_MAP_ID ));
		coreIds.add( new Integer( DEFINITION_SEQUENCE_ID ));
		coreIds.add( new Integer( DEFINITION_ABSTRACT_ID ));	
		return coreIds;
	}

	public static int EMPTYID = 1;
	public static int U8BID = 2;
	public static int U16BID = 3;
	public static int BASICID = 4;
	public static int ABSTRACTID = 5;
	public static int MAP_ID = 6;
	public static int EXPRESSIONID = 7;
	public static int SEQUENCEID = 8;
	public static int REFERENCEID = 9;
	public static int NAMEID = 10;
	public static int ENCODEDID = 11;
	public static int ARRAYID = 12;
	public static int EXPRESSION_REFERENCE_ID = 13;
	public static int EXPRESSION_SEQUENCE_ID = 14;
	public static int EXPRESSION_ARRAY_ID = 15;
	public static int EXPRESSION_ENCODED_ID = 16;
	public static int DEFINITION_ID = 17;
	public static int DEFINITION_BASIC_ID = 18;
	public static int DEFINITION_MAP_ID = 19;
	public static int DEFINITION_SEQUENCE_ID = 20;
	public static int DEFINITION_ABSTRACT_ID = 21;
}
