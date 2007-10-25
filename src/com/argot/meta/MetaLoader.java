/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

package com.argot.meta;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeReaderAuto;

import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.BigEndianUnsignedShort;
import com.argot.common.Empty;
import com.argot.common.U8Ascii;

public class MetaLoader
implements TypeLibraryLoader
{
	public final String DICTIONARY = "meta.dictionary";
	
	public String getName()
	{
		return DICTIONARY;
	}

	public void load( TypeLibrary library ) throws TypeException
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

		library.register( MetaBasic.TYPENAME, basicDef, new TypeReaderAuto(MetaBasic.class), new MetaBasic.MetaBasicTypeWriter(), MetaBasic.class );
			
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
		
		library.register( "meta.encoding", encodingDef, new TypeReaderAuto(MetaEncoding.class), new MetaEncoding.MetaEncodingTypeWriter(), MetaEncoding.class );
			
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
		
		library.register( "meta.sequence", seqDef, new TypeReaderAuto(MetaSequence.class), new MetaSequence.MetaSequenceTypeWriter(), MetaSequence.class );
			
		MetaDefinition arrayDef = new MetaSequence(
			new MetaExpression[] {
				new MetaReference(library.getId("meta.expression"),"size"),
				new MetaReference(library.getId("meta.expression"),"type")
			}
		);

		library.register( "meta.array", arrayDef, new TypeReaderAuto(MetaArray.class), new MetaArray.MetaArrayTypeWriter(), MetaArray.class );
						
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

}
