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
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeReaderAuto;
import com.argot.auto.TypeBeanMarshaller;
import com.argot.common.Empty;
import com.argot.common.U8Ascii;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.dictionary.TypeMapMarshaller;

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
		int id;
		
		// Empty 1
		if ( library.getTypeState( Empty.TYPENAME ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
		    library.reserve( Empty.TYPENAME );
		    library.reserve( MetaFixedWidth.TYPENAME );
		    
			Empty te = new Empty();
			
			MetaDefinition emptyDef = new MetaFixedWidth( (byte)0,
				new MetaFixedWidthAttribute[] {
					new MetaFixedWidthAttributeSize((short)0)
			});
			
			id = library.register( Empty.TYPENAME, emptyDef, te,te,te.getClass() );
			library.setSimpleType(id,true);
		}

		// uint8 2
		if ( library.getTypeState( UInt8.TYPENAME ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			UInt8 bbe = new UInt8();
			
			MetaDefinition u8def = new MetaFixedWidth((byte)8,
				new MetaFixedWidthAttribute[] {
					new MetaFixedWidthAttributeSize((short)8),
					new MetaFixedWidthAttributeInteger(),
					new MetaFixedWidthAttributeUnsigned(),
					new MetaFixedWidthAttributeBigEndian()
			});

			id = library.register( UInt8.TYPENAME, u8def, bbe, bbe, null );
			library.setSimpleType(id,true);
		}
		
		// uint16 3
		if ( library.getTypeState( UInt16.TYPENAME ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			UInt16 bbs = new UInt16();
			
			MetaDefinition u16def = new MetaFixedWidth( (byte)16,
				new MetaFixedWidthAttribute[] {
					new MetaFixedWidthAttributeSize((short)16),
					new MetaFixedWidthAttributeInteger(),
					new MetaFixedWidthAttributeUnsigned(),
					new MetaFixedWidthAttributeBigEndian()
			});			
			
			id = library.register( UInt16.TYPENAME, u16def, bbs, bbs, null );
			library.setSimpleType(id,true);
		}
		

	    library.reserve( MetaSequence.TYPENAME );
	    library.reserve( MetaReference.TYPENAME );
	    library.reserve( MetaTag.TYPENAME );
	    library.reserve( MetaArray.TYPENAME );

	    // meta.id 4
	    MetaDefinition metaIdDef = new MetaReference( library.getId( UInt16.TYPENAME ));
	    library.register( "meta.id", metaIdDef, new UInt16(), new UInt16(), null );

		// meta.abstract 5
		MetaDefinition abstractDef = new MetaReference( library.getId(Empty.TYPENAME) );		
		library.register( MetaAbstract.TYPENAME, abstractDef, (TypeLibraryReader) new MetaAbstract.MetaAbstractTypeReader(), (TypeLibraryWriter) new MetaAbstract.MetaAbstractTypeWriter(), MetaAbstract.class );
	    
	    // meta.map 6
		MetaDefinition metaMapDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "abstract", new MetaReference( library.getId( "meta.id" ))),
					new MetaTag( "concrete", new MetaReference( library.getId( "meta.id" )))
				});
		library.register( MetaMap.TYPENAME, metaMapDef, new MetaMap.MetaMapTypeReader(), new MetaMap.MetaMapTypeWriter(), MetaMap.class );

			
	    library.reserve( MetaEncoding.TYPENAME );

		// u8ascii 7
		U8Ascii u8Ascii = new U8Ascii();
		MetaDefinition u8asciiDef = new MetaSequence(
			new MetaExpression[] {
				new MetaEncoding(
					new MetaArray(
						new MetaReference( library.getId(UInt8.TYPENAME)),
						new MetaReference( library.getId(UInt8.TYPENAME))
					),
					"ISO646-US"
				)
			}
		);
		id = library.register( U8Ascii.TYPENAME, u8asciiDef, u8Ascii, u8Ascii, null );
		library.setSimpleType(id,true);
		
		// meta.name 8
		MetaDefinition nameDef = new MetaReference( library.getId(U8Ascii.TYPENAME));
		library.register( "meta.name", nameDef, u8Ascii,u8Ascii, null );

		// meta.expression 9
		MetaDefinition exprDef = new MetaAbstract();		
		library.register( MetaExpression.TYPENAME, exprDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.reference 10
		MetaDefinition refDef = new MetaSequence( new MetaExpression[] { new MetaReference( library.getId( UInt16.TYPENAME ))});
		library.register( MetaReference.TYPENAME, refDef, (TypeLibraryReader) new MetaReference.MetaReferenceTypeReader(), (TypeLibraryWriter) new MetaReference.MetaReferenceTypeWriter(), MetaReference.class );
		
		// meta.tag 11
		MetaDefinition tagDef = new MetaSequence( new MetaExpression[] { 
				new MetaTag( "name", new MetaReference(library.getId(U8Ascii.TYPENAME))),
				new MetaTag( "data", new MetaReference(library.getId(MetaExpression.TYPENAME)))
		});
		library.register( MetaTag.TYPENAME, tagDef, new MetaTag.MetaTagTypeReader(), new MetaTag.MetaTagTypeWriter(), MetaTag.class );
		
		// meta.sequence 12
		MetaDefinition seqDef = new MetaSequence( new MetaExpression[] { new MetaArray(
					new MetaReference( library.getId(UInt8.TYPENAME)),
					new MetaReference( library.getId(MetaExpression.TYPENAME))
				)});
		library.register( MetaSequence.TYPENAME, seqDef, new MetaSequence.MetaSequenceTypeReader(), new MetaSequence.MetaSequenceTypeWriter(), MetaSequence.class );
			
		// meta.array 13
		MetaDefinition arrayDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "size", new MetaReference(library.getId(MetaExpression.TYPENAME))),
				new MetaTag( "type", new MetaReference(library.getId(MetaExpression.TYPENAME)))
			}
		);
		library.register( MetaArray.TYPENAME, arrayDef, new MetaArray.MetaArrayTypeReader(), new MetaArray.MetaArrayTypeWriter(), MetaArray.class );

		// meta.envelop 14
		MetaDefinition meDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "size", new MetaReference(library.getId(MetaExpression.TYPENAME))),
					new MetaTag( "type",new MetaReference(library.getId(MetaExpression.TYPENAME)))
				}
			);
		library.register( MetaEnvelop.TYPENAME, meDef, new MetaEnvelop.MetaEnvelopTypeReader(), new MetaEnvelop.MetaEnvelopTypeWriter(), MetaEnvelop.class );
		
		// meta.encoding 15
		MetaDefinition encodingDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "data", new MetaReference( library.getId( MetaExpression.TYPENAME ))),
				new MetaTag( "encoding", new MetaReference( library.getId(U8Ascii.TYPENAME)))
			}
		);
		library.register( MetaEncoding.TYPENAME, encodingDef, new MetaEncoding.MetaEncodingTypeReader(), new MetaEncoding.MetaEncodingTypeWriter(), MetaEncoding.class );

	    library.reserve( MetaFixedWidthAttribute.TYPENAME );
	    
	    // meta.fixed_width 16
		MetaDefinition basicDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "size", new MetaReference( library.getId( UInt8.TYPENAME ))),
				new MetaTag( "flags",
					new MetaArray(
						new MetaReference( library.getId( UInt8.TYPENAME )),
						new MetaReference( library.getId( MetaFixedWidthAttribute.TYPENAME ))
					)
				)
			}
		);	
		library.register( MetaFixedWidth.TYPENAME, basicDef, new TypeReaderAuto(MetaFixedWidth.class), new MetaFixedWidth.MetaBasicTypeWriter(), MetaFixedWidth.class );

	    // meta.fixed_width.attribute 17
	    MetaDefinition fwAttribute = new MetaAbstract();
	    library.register( MetaFixedWidthAttribute.TYPENAME, fwAttribute, new MetaMarshaller(),new MetaMarshaller(), null );
		
	    // meta.fixed_width.attribute.size 18
	    MetaDefinition fwaSize = new MetaSequence( new MetaExpression[] { new MetaTag( "size", new MetaReference( library.getId( UInt8.TYPENAME ))) });
	    library.register( MetaFixedWidthAttributeSize.TYPENAME, fwaSize, new TypeReaderAuto(MetaFixedWidthAttributeSize.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeSize.class );	    

	    // meta.fixed_width.attribute.signed 19
	    MetaDefinition fwaSigned = new MetaSequence( new MetaExpression[] {} );
	    library.register( MetaFixedWidthAttributeSigned.TYPENAME, fwaSigned, new TypeReaderAuto(MetaFixedWidthAttributeSigned.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeSigned.class );	    

	    // meta.fixed_width.attribute.unsigned 20
	    MetaDefinition fwaUnsigned = new MetaSequence( new MetaExpression[] {} );
	    library.register( MetaFixedWidthAttributeUnsigned.TYPENAME, fwaUnsigned, new TypeReaderAuto(MetaFixedWidthAttributeUnsigned.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeUnsigned.class );	    
	    
	    // meta.fixed_width.attribute.integer 21
	    MetaDefinition fwaInteger = new MetaSequence( new MetaExpression[] {} );
	    library.register( MetaFixedWidthAttributeInteger.TYPENAME, fwaInteger, new TypeReaderAuto(MetaFixedWidthAttributeInteger.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeInteger.class );	    
	    
	    // meta.fixed_width.attribute.bigendian 22
	    MetaDefinition fwaBigEndian = new MetaSequence( new MetaExpression[] {} );
	    library.register( MetaFixedWidthAttributeBigEndian.TYPENAME, fwaBigEndian, new TypeReaderAuto(MetaFixedWidthAttributeBigEndian.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeBigEndian.class );	    

	    // meta.fixed_width.attribute#meta.fixed_width.attribute.size 23
		MetaMap fwaSizeMap = new MetaMap(library.getId( MetaFixedWidthAttribute.TYPENAME), library.getId(MetaFixedWidthAttributeSize.TYPENAME));
		library.register( fwaSizeMap.getMapTypeName(library), fwaSizeMap, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.fixed_width.attribute#meta.fixed_width.attribute.signed 24
		MetaMap fwaSignedMap = new MetaMap(library.getId( MetaFixedWidthAttribute.TYPENAME), library.getId(MetaFixedWidthAttributeSigned.TYPENAME));
		library.register( fwaSignedMap.getMapTypeName(library), fwaSignedMap, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.fixed_width.attribute#meta.fixed_width.attribute.unsigned 25
		MetaMap fwaUnsignedMap = new MetaMap(library.getId( MetaFixedWidthAttribute.TYPENAME), library.getId(MetaFixedWidthAttributeUnsigned.TYPENAME));
		library.register( fwaUnsignedMap.getMapTypeName(library), fwaUnsignedMap, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.fixed_width.attribute#meta.fixed_width.attribute.integer 26
		MetaMap fwaIntegerMap = new MetaMap(library.getId( MetaFixedWidthAttribute.TYPENAME), library.getId(MetaFixedWidthAttributeInteger.TYPENAME));
		library.register( fwaIntegerMap.getMapTypeName(library), fwaIntegerMap, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.fixed_width.attribute#meta.fixed_width.attribute.bigendian 27
		MetaMap fwaBigEndianMap = new MetaMap(library.getId( MetaFixedWidthAttribute.TYPENAME), library.getId(MetaFixedWidthAttributeBigEndian.TYPENAME));
		library.register( fwaBigEndianMap.getMapTypeName(library), fwaBigEndianMap, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.expression#meta.reference 28
		MetaMap exprRefDef = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaReference.TYPENAME));
		library.register( exprRefDef.getMapTypeName(library), exprRefDef, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.expression#meta.tag 29
		MetaMap exprTagDef = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaTag.TYPENAME));
		library.register( exprTagDef.getMapTypeName(library), exprTagDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.expression#meta.sequence 30
		MetaMap exprExpSeq = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaSequence.TYPENAME));		
		library.register( exprExpSeq.getMapTypeName(library), exprExpSeq, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.expression#meta.array 31
		MetaMap exprDefArr = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaArray.TYPENAME));		
		library.register( exprDefArr.getMapTypeName(library), exprDefArr, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.expression#meta.envelop 32
		MetaMap exprEnvDef = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaEnvelop.TYPENAME));		
		library.register( exprEnvDef.getMapTypeName(library), exprEnvDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.expression#meta.encoding 33
		MetaMap exprDefEnc = new MetaMap( library.getId(MetaExpression.TYPENAME), library.getId(MetaEncoding.TYPENAME));		
		library.register( exprDefEnc.getMapTypeName(library), exprDefEnc, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.definition 34
		MetaAbstract mm = new MetaAbstract();
		library.register( MetaDefinition.TYPENAME, mm, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// meta.definition#meta.fixed_width 35
		MetaMap mmbasic = new MetaMap(library.getId(MetaDefinition.TYPENAME), library.getId(MetaFixedWidth.TYPENAME));
		library.register( mmbasic.getMapTypeName(library), mmbasic, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.definition#meta.map 36
		MetaMap exprDefMap = new MetaMap( library.getId(MetaDefinition.TYPENAME), library.getId(MetaMap.TYPENAME));		
		library.register( exprDefMap.getMapTypeName(library), exprDefMap, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.definition#meta.abstract 37
		MetaMap exprDefAbs = new MetaMap( library.getId(MetaDefinition.TYPENAME), library.getId(MetaAbstract.TYPENAME));		
		library.register( exprDefAbs.getMapTypeName(library), exprDefAbs, new MetaMarshaller(),new MetaMarshaller(), null );

		// meta.definition#meta.expression 38
		MetaMap exprDefSeq = new MetaMap( library.getId(MetaDefinition.TYPENAME), library.getId(MetaExpression.TYPENAME));		
		library.register( exprDefSeq.getMapTypeName(library), exprDefSeq, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// meta.definition.envelop 39
		MetaDefinition dDef = new MetaEnvelop(
				new MetaReference(library.getId(UInt16.TYPENAME) ),
				new MetaReference(library.getId(MetaDefinition.TYPENAME))
		);			  
		library.register( "meta.definition.envelop",  dDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// dictionary.entry 40
		MetaDefinition entryDef =new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "id", new MetaReference(library.getId(UInt16.TYPENAME))),
				new MetaTag( "name", new MetaReference(library.getId("meta.name"))),
				new MetaTag( "definition", new MetaReference(library.getId("meta.definition.envelop"))) 
			}
	    );
		library.register( "dictionary.entry", entryDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// dictionary.entry.list 41
		MetaDefinition dmDef =	new MetaSequence(
			new MetaExpression[] {
				new MetaArray(
					new MetaReference(library.getId(UInt16.TYPENAME) ),
					new MetaReference(library.getId("dictionary.entry"))
				)				  
			}
		);		
		library.register( "dictionary.entry.list", dmDef, new TypeReaderAuto(TypeMap.class), new TypeMapMarshaller(), null );
		
	}

}
