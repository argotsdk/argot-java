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
import com.argot.common.U8Ascii;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaArray;
import com.argot.meta.MetaBasic;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaEncoding;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaMap;
import com.argot.meta.MetaMarshaller;
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
	
	public static void loadLibrary( TypeLibrary library )
	throws TypeException
	{
	
		if ( library.getTypeState( "empty" ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
		    library.reserve( "empty");
		    library.reserve( "meta.basic");
		    
			Empty te = new Empty();
			MetaDefinition emptyDef = new MetaBasic( (byte)0, (byte)0);
			library.register( Empty.TYPENAME, emptyDef, te,te,te.getClass() );
			
		}

		if ( library.getTypeState( BigEndianUnsignedByte.TYPENAME ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			BigEndianUnsignedByte bbe = new BigEndianUnsignedByte();	
			MetaDefinition u8def = new MetaBasic((byte)8,(byte)0);				

			library.register( BigEndianUnsignedByte.TYPENAME, u8def, bbe, bbe, null );
		}
		
		if ( library.getTypeState( BigEndianUnsignedShort.TYPENAME ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			BigEndianUnsignedShort bbs = new BigEndianUnsignedShort();
			MetaDefinition u16def = new MetaBasic( (byte)16,(byte)0);			
			library.register( BigEndianUnsignedShort.TYPENAME, u16def, bbs, bbs, null ); 			
		}
		

	    library.reserve("meta.sequence");
	    library.reserve("meta.reference");
	    
		MetaDefinition basicDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference( library.getId( "u8" ),"size"),
				new MetaReference( library.getId( "u8" ),"flags"),
			}
		);	

		library.register( MetaBasic.TYPENAME, basicDef, new MetaBasic.MetaBasicTypeReader(), new MetaBasic.MetaBasicTypeWriter(), MetaBasic.class );
			
	    library.reserve( "meta.encoding");
	    library.reserve( "meta.array");
		    
		U8Ascii u8Ascii = new U8Ascii();
		
		MetaDefinition nameDef = new MetaSequence(
			new MetaExpression[] {
				new MetaEncoding(
					new MetaArray(
						new MetaReference( library.getId("u8"),"size"),
						new MetaReference( library.getId("u8"),"data")
					),
					"ISO646-US"
				)
			}
		);
			
		library.register( "meta.name", nameDef, u8Ascii,u8Ascii, null );
			
		MetaDefinition abstractDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference( library.getId("empty"), "abstract")
			}
		);
		
		library.register( "meta.abstract", abstractDef, new MetaAbstract.MetaAbstractTypeReader(), new MetaAbstract.MetaAbstractTypeWriter(), MetaAbstract.class );

		MetaDefinition metaMapDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference( library.getId( "u16" ),"abstract" ),
				new MetaReference( library.getId( "u16" ),"concrete")
			}
		);

		library.register( "meta.map", metaMapDef, new MetaMap.MetaMapTypeReader(), new MetaMap.MetaMapTypeWriter(), MetaMap.class );
						
		MetaDefinition exprDef = new MetaAbstract();
		
		library.register( "meta.expression", exprDef, new MetaMarshaller(),new MetaMarshaller(), null );
			
		MetaDefinition encodingDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference( library.getId("meta.expression"),"data"),
				new MetaReference( library.getId("meta.name"), "encoding")
			}
		);
		
		library.register( "meta.encoding", encodingDef, new MetaEncoding.MetaEncodingTypeReader(), new MetaEncoding.MetaEncodingTypeWriter(), MetaEncoding.class );
			
		MetaDefinition refDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference( library.getId( "u16" ),"type" ),
				new MetaReference( library.getId( "meta.name" ),"name")
			}
		);
		
		library.register( "meta.reference", refDef, new MetaReference.MetaReferenceTypeReader(), new MetaReference.MetaReferenceTypeWriter(), MetaReference.class );
					
		MetaDefinition seqDef = new MetaSequence(
			new MetaExpression[] {
				new MetaArray(
					new MetaReference( library.getId("u8"), "size"),
					new MetaReference( library.getId("meta.expression"), "type")
				)
			}
		);
		
		library.register( "meta.sequence", seqDef, new MetaSequence.MetaSequenceTypeReader(), new MetaSequence.MetaSequenceTypeWriter(), MetaSequence.class );
			
		MetaDefinition arrayDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference(library.getId("meta.expression"),"size"),
				new MetaReference(library.getId("meta.expression"),"type")
			}
		);

		library.register( "meta.array", arrayDef, new MetaArray.MetaArrayTypeReader(), new MetaArray.MetaArrayTypeWriter(), MetaArray.class );
						
		MetaMap exprRefDef = new MetaMap( library.getId("meta.expression"), library.getId("meta.reference"));
		library.register( "meta.expression#reference", exprRefDef, new MetaMarshaller(),new MetaMarshaller(), null );

		MetaMap exprExpSeq = new MetaMap( library.getId("meta.expression"), library.getId("meta.sequence"));		
		library.register( "meta.expression#sequence", exprExpSeq, new MetaMarshaller(),new MetaMarshaller(), null );

		MetaMap exprDefArr = new MetaMap( library.getId("meta.expression"), library.getId("meta.array"));		
		library.register( "meta.expression#array", exprDefArr, new MetaMarshaller(),new MetaMarshaller(), null );

		MetaMap exprDefEnc = new MetaMap( library.getId("meta.expression"), library.getId("meta.encoding"));		
		library.register( "meta.expression#encoding", exprDefEnc, new MetaMarshaller(),new MetaMarshaller(), null );

		MetaAbstract mm = new MetaAbstract();
		library.register( "meta.definition", mm, new MetaMarshaller(), new MetaMarshaller(), null );
		
		MetaMap mmbasic = new MetaMap(library.getId("meta.definition"), library.getId("meta.basic"));
		library.register( "meta.definition#basic", mmbasic, new MetaMarshaller(),new MetaMarshaller(), null );
			
		MetaMap exprDefSeq = new MetaMap( library.getId("meta.definition"), library.getId("meta.sequence"));		
		library.register( "meta.definition#sequence", exprDefSeq, new MetaMarshaller(),new MetaMarshaller(), null );
			
		MetaMap exprDefMap = new MetaMap( library.getId("meta.definition"), library.getId("meta.map"));		
		library.register( "meta.definition#map", exprDefMap, new MetaMarshaller(),new MetaMarshaller(), null );
			
		MetaMap exprDefAbs = new MetaMap( library.getId("meta.definition"), library.getId("meta.abstract"));		
		library.register( "meta.definition#abstract", exprDefAbs, new MetaMarshaller(),new MetaMarshaller(), null );
			
		MetaMap exprDefRef = new MetaMap( library.getId("meta.definition"), library.getId("meta.reference"));		
		library.register( "meta.definition#reference", exprDefRef, new MetaMarshaller(),new MetaMarshaller(), null );
				
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
