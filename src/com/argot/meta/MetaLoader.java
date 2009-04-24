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

package com.argot.meta;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.auto.TypeBeanMarshaller;
import com.argot.auto.TypeReaderAuto;
import com.argot.auto.TypeSimpleReader;
import com.argot.auto.TypeSimpleWriter;
import com.argot.common.Empty;
import com.argot.common.U8Utf8;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.dictionary.TypeMapMarshaller;

public class MetaLoader
implements TypeLibraryLoader
{
	public final String DICTIONARY = "meta.dictionary";
	public final String VERSION = "1.3";
		
	public String getName()
	{
		return DICTIONARY;
	}
	
	public void load( TypeLibrary library ) throws TypeException
	{
		int id;
			
		// 0. empty
		int emptyId = library.register( new DictionaryName( Empty.TYPENAME ), new MetaIdentity() );  
		// 1. uint8
		int uint8Id = library.register( new DictionaryName( UInt8.TYPENAME ), new MetaIdentity() );  
		// 2. uint16
		int uint16Id = library.register( new DictionaryName( UInt16.TYPENAME ), new MetaIdentity() ); 
		// 3. meta.id
		int metaId = library.register( new DictionaryName( "meta.id" ), new MetaIdentity() );  
		// 4. meta.abstract.map
		int abstractMapId = library.register( new DictionaryName( MetaAbstractMap.TYPENAME ), new MetaIdentity() ); 
		// 5. meta.abstract
		int abstractId = library.register( new DictionaryName( MetaAbstract.TYPENAME ), new MetaIdentity() );  
		// 6. u8ascii
		int u8utf8Id = library.register( new DictionaryName( U8Utf8.TYPENAME ), new MetaIdentity() );
		// 7. meta.name
		int metaNamePartId = library.register( new DictionaryName( "meta.name_part" ), new MetaIdentity() );
		// 7. meta.name
		int metaNameId = library.register( new DictionaryName( "meta.name" ), new MetaIdentity() );
		// 8. meta.version
		int metaVersionId = library.register( new DictionaryName( MetaVersion.TYPENAME ), new MetaIdentity() );
		// 9. meta.definition
		int metaDefinitionId = library.register( new DictionaryName( MetaDefinition.TYPENAME ), new MetaIdentity() );
		// 10. meta.identity
		int metaIdentityId = library.register( new DictionaryName( MetaIdentity.TYPENAME ), new MetaIdentity() );
		// 11. meta.expression
		int metaExpressionId = library.register( new DictionaryName( MetaExpression.TYPENAME ), new MetaIdentity() );
		// 12. meta.reference
		int metaReferenceId = library.register( new DictionaryName( MetaReference.TYPENAME ), new MetaIdentity() );
		// 13. meta.tag
		int metaTagId = library.register( new DictionaryName( MetaTag.TYPENAME ), new MetaIdentity() );
		// 14. meta.sequence
		int metaSequenceId = library.register( new DictionaryName( MetaSequence.TYPENAME ), new MetaIdentity() );
		// 15. meta.array
		int metaArrayId = library.register( new DictionaryName( MetaArray.TYPENAME ), new MetaIdentity() );
		// 16. meta.envelop
		int metaEnvelopId = library.register( new DictionaryName( MetaEnvelop.TYPENAME ), new MetaIdentity() );
		// 17. meta.encoding
		int metaEncodingId = library.register( new DictionaryName( MetaEncoding.TYPENAME ), new MetaIdentity() );
		// 18. meta.fixed_width
		int metaFixedWidthId = library.register( new DictionaryName( MetaFixedWidth.TYPENAME ), new MetaIdentity() );
		// 19. meta.fixed_width.attribute
		int metaFixedWidthAttributeId = library.register( new DictionaryName( MetaFixedWidthAttribute.TYPENAME ), new MetaIdentity() );
		// 20. meta.fixed_width.attribute.size
		int metaFixedWidthAttributeSizeId = library.register( new DictionaryName( MetaFixedWidthAttributeSize.TYPENAME ), new MetaIdentity() );
		// 21. meta.fixed_width.attribute.integer
		int metaFixedWidthAttributeIntegerId = library.register( new DictionaryName( MetaFixedWidthAttributeInteger.TYPENAME ), new MetaIdentity() );
		// 22. meta.fixed_width.attribute.unsigned
		int metaFixedWidthAttributeUnsignedId = library.register( new DictionaryName( MetaFixedWidthAttributeUnsigned.TYPENAME ), new MetaIdentity() );
		// 23. meta.fixed_width.attribute.bigendian
		int metaFixedWidthAttributeBigEndianId = library.register( new DictionaryName( MetaFixedWidthAttributeBigEndian.TYPENAME ), new MetaIdentity() );
		// 24. dictionary.name
		int dictionaryNameId = library.register( new DictionaryName( DictionaryName.TYPENAME), new MetaIdentity() );
		// 25. dictionary.name
		int dictionaryDefinitionId = library.register( new DictionaryName( DictionaryDefinition.TYPENAME), new MetaIdentity() );
		// 26. dictionary.relation
		int dictionaryRelationId = library.register( new DictionaryName( DictionaryRelation.TYPENAME), new MetaIdentity() );
		// 27. dictionary.location
		int dictionaryLocationId = library.register( new DictionaryName( DictionaryLocation.TYPENAME), new MetaIdentity() );
		// 28. meta.definition.envelop
		int metaDefinitionEnvelopId = library.register( new DictionaryName( "meta.definition.envelop" ), new MetaIdentity() );
		// 29. dictionary.entry
		int dictionaryEntryId = library.register( new DictionaryName("dictionary.entry"), new MetaIdentity() );
		// 30. dictionary.entry.list
		int dictionaryEntryListId = library.register( new DictionaryName( "dictionary.entry.list"), new MetaIdentity() );
		
		// 0 - 31. Empty 
		Empty te = new Empty();
		
		MetaDefinition emptyDef = new MetaFixedWidth( 0,
			new MetaFixedWidthAttribute[] {
				new MetaFixedWidthAttributeSize(0)
		});
		
		id = library.register(new DictionaryDefinition(emptyId,Empty.TYPENAME, "1.3"), emptyDef, new TypeSimpleReader(te),new TypeSimpleWriter(te),te.getClass() );
		library.setSimpleType(id,true);

		// 1 - 32. uint8
		UInt8 bbe = new UInt8();
		
		MetaDefinition u8def = new MetaFixedWidth(8,
			new MetaFixedWidthAttribute[] {
				new MetaFixedWidthAttributeSize(8),
				new MetaFixedWidthAttributeInteger(),
				new MetaFixedWidthAttributeUnsigned(),
				new MetaFixedWidthAttributeBigEndian()
		});

		id = library.register( new DictionaryDefinition(uint8Id,UInt8.TYPENAME, "1.3"), u8def, new TypeSimpleReader(bbe), new TypeSimpleWriter(bbe), null );
		library.setSimpleType(id,true);
		
		// 2 - 33. uint16
		UInt16 bbs = new UInt16();
		
		MetaDefinition u16def = new MetaFixedWidth( 16,
			new MetaFixedWidthAttribute[] {
				new MetaFixedWidthAttributeSize(16),
				new MetaFixedWidthAttributeInteger(),
				new MetaFixedWidthAttributeUnsigned(),
				new MetaFixedWidthAttributeBigEndian()
		});
		
		id = library.register( new DictionaryDefinition(uint16Id,UInt16.TYPENAME, "1.3"), u16def, new TypeSimpleReader(bbs), new TypeSimpleWriter(bbs), null );
		library.setSimpleType(id,true);
		
	    // 3 - 34. meta.id
	    MetaDefinition metaIdDef = new MetaReference( uint16Id );
	    library.register( new DictionaryDefinition(metaId,"meta.id", "1.3"), metaIdDef, new TypeSimpleReader(new UInt16()), new TypeSimpleWriter(new UInt16()), null );

	    // 4 - 35. meta.abstract.map 
		MetaDefinition metaMapDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "id", new MetaReference( metaId)),
				});
		library.register( new DictionaryDefinition(abstractMapId,MetaAbstractMap.TYPENAME, "1.3"), metaMapDef, new TypeSimpleReader(new MetaAbstractMap.MetaMapTypeReader()), new TypeSimpleWriter(new MetaAbstractMap.MetaMapTypeWriter()), MetaAbstractMap.class );

		// 5 - 36. meta.abstract
		MetaDefinition abstractDef = new MetaSequence(
				new MetaExpression[] {
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( abstractMapId )
					)	
				});
		library.register( new DictionaryDefinition(abstractId,MetaAbstract.TYPENAME, "1.3"), abstractDef, (TypeLibraryReader) new MetaAbstract.MetaAbstractTypeReader(), (TypeLibraryWriter) new MetaAbstract.MetaAbstractTypeWriter(), MetaAbstract.class );
	    

		// 6 - 37. u8ascii
		U8Utf8 u8utf8 = new U8Utf8();
		MetaDefinition u8asciiDef = 
			new MetaEncoding(
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( uint8Id )
					),
					"UTF-8"
				);
		id = library.register(new DictionaryDefinition(u8utf8Id,U8Utf8.TYPENAME, "1.3"), u8asciiDef, new TypeSimpleReader(u8utf8), new TypeSimpleWriter(u8utf8), String.class );
		library.setSimpleType(id,true);
		
		// 7 - 38. meta.name_part
		MetaDefinition namePartDef = new MetaReference( u8utf8Id );
		library.register(new DictionaryDefinition( metaNamePartId,"meta.name_part", "1.3"), namePartDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		
		// 8 - 38. meta.name
		MetaDefinition nameDef = new MetaArray(
				new MetaReference( uint8Id ),
				new MetaReference( metaNamePartId ));
		
		library.register(new DictionaryDefinition( metaNameId,"meta.name", "1.3"), nameDef, new MetaName.MetaNameTypeLibraryReader(),new MetaName.MetaNameTypeWriter(), MetaName.class );

		// 8 - 39. meta.version
		//MetaDefinition versionDef = new MetaReference( u8asciiId );
		MetaDefinition versionDef = 
			new MetaSequence(
					new MetaExpression[] {
							new MetaTag( "major", new MetaReference( uint8Id )),
							new MetaTag( "minor", new MetaReference( uint8Id ))
					}
			);
		library.register(new DictionaryDefinition(metaVersionId,MetaVersion.TYPENAME, "1.3"), versionDef, new TypeBeanMarshaller(),new TypeBeanMarshaller(), MetaVersion.class );


		// 11 - 42. meta.expression
		MetaDefinition exprDef = new MetaAbstract(
				new MetaAbstractMap[] {
						new MetaAbstractMap( metaReferenceId ),
						new MetaAbstractMap( metaTagId ),
						new MetaAbstractMap( metaSequenceId ),
						new MetaAbstractMap( metaArrayId ), 
						new MetaAbstractMap( metaEnvelopId ),
						new MetaAbstractMap( metaEncodingId )
				});		
		int exprDefId = library.register(new DictionaryDefinition( metaExpressionId,MetaExpression.TYPENAME, "1.3"), exprDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		
		// 9 - 40. meta.definition
		MetaDefinition defDef = new MetaAbstract(
				new MetaAbstractMap[] {
						new MetaAbstractMap( metaFixedWidthId ),
						new MetaAbstractMap( abstractId ),
						new MetaAbstractMap( abstractMapId ),
						new MetaAbstractMap( exprDefId ), 
						new MetaAbstractMap( metaIdentityId )
				});		
		library.register(new DictionaryDefinition( metaDefinitionId,MetaDefinition.TYPENAME, "1.3"), defDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// 10 - 41. meta.identity
		MetaDefinition identDef = new MetaSequence( new MetaExpression[] {} );
		library.register(new DictionaryDefinition( metaIdentityId,MetaIdentity.TYPENAME, "1.3"), identDef, new TypeReaderAuto(MetaIdentity.class), new TypeBeanMarshaller(), MetaIdentity.class );
		
		// 12 - 43. meta.reference
		MetaDefinition refDef = new MetaSequence( 
				new MetaExpression[] { 
						new MetaReference( metaId )});
		library.register( new DictionaryDefinition(metaReferenceId, MetaReference.TYPENAME,"1.3"), refDef, (TypeLibraryReader) new MetaReference.MetaReferenceTypeReader(), (TypeLibraryWriter) new MetaReference.MetaReferenceTypeWriter(), MetaReference.class );
		
		// 13 - 44. meta.tag
		MetaDefinition tagDef = new MetaSequence( new MetaExpression[] { 
				new MetaTag( "name", new MetaReference( u8utf8Id )),
				new MetaTag( "data", new MetaReference( metaExpressionId ))
		});
		library.register(new DictionaryDefinition(metaTagId,MetaTag.TYPENAME, "1.3"), tagDef, new MetaTag.MetaTagTypeReader(), new MetaTag.MetaTagTypeWriter(), MetaTag.class );
		
		// 14 - 45. meta.sequence
		// TODO Remove MetaSequence.  Requires better Marshaller classes.
		MetaDefinition seqDef = new MetaSequence( new MetaExpression[] { new MetaArray(
					new MetaReference( uint8Id),
					new MetaReference( metaExpressionId))});
		library.register( new DictionaryDefinition( metaSequenceId,MetaSequence.TYPENAME, "1.3"), seqDef, new MetaSequence.MetaSequenceTypeReader(), new MetaSequence.MetaSequenceTypeWriter(), MetaSequence.class );
			
		// 15 - 46. meta.array
		MetaDefinition arrayDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "size", new MetaReference( metaExpressionId)),
				new MetaTag( "type", new MetaReference( metaExpressionId))
			}
		);
		library.register( new DictionaryDefinition( metaArrayId,MetaArray.TYPENAME, "1.3"), arrayDef, new MetaArray.MetaArrayTypeReader(), new MetaArray.MetaArrayTypeWriter(), MetaArray.class );

		// 16 - 47. meta.envelop
		MetaDefinition meDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "size", new MetaReference( metaExpressionId)),
					new MetaTag( "type",new MetaReference( metaExpressionId))
				}
			);
		library.register(new DictionaryDefinition( metaEnvelopId,MetaEnvelop.TYPENAME, "1.3"), meDef, new MetaEnvelop.MetaEnvelopTypeReader(), new MetaEnvelop.MetaEnvelopTypeWriter(), MetaEnvelop.class );
		
		// 17 - 48. meta.encoding
		MetaDefinition encodingDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "data", new MetaReference( metaExpressionId)),
				new MetaTag( "encoding", new MetaReference( u8utf8Id ))
			}
		);
		library.register( new DictionaryDefinition(metaEncodingId, MetaEncoding.TYPENAME, "1.3"), encodingDef, new MetaEncoding.MetaEncodingTypeReader(), new MetaEncoding.MetaEncodingTypeWriter(), MetaEncoding.class );

	    // 18 - 49. meta.fixed_width
		MetaDefinition basicDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "size", new MetaReference( uint16Id )),
				new MetaTag( "flags",
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( metaFixedWidthAttributeId )
					)
				)
			}
		);	
		library.register( new DictionaryDefinition(metaFixedWidthId, MetaFixedWidth.TYPENAME, "1.3"), basicDef, new TypeReaderAuto(MetaFixedWidth.class), new MetaFixedWidth.MetaBasicTypeWriter(), MetaFixedWidth.class );

	    // 19 - 50. meta.fixed_width.attribute 
	    MetaDefinition fwAttribute = new MetaAbstract(
	    		new MetaAbstractMap[] {
	    				new MetaAbstractMap( metaFixedWidthAttributeSizeId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeIntegerId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeUnsignedId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeBigEndianId )
	    		});
	    library.register( new DictionaryDefinition(metaFixedWidthAttributeId,MetaFixedWidthAttribute.TYPENAME, "1.3"), fwAttribute, new MetaMarshaller(),new MetaMarshaller(), null );
		
	    // 20 - 51. meta.fixed_width.attribute.size
	    MetaDefinition fwaSize = new MetaSequence( new MetaExpression[] { new MetaTag( "size", new MetaReference( uint16Id )) });
	    library.register( new DictionaryDefinition(metaFixedWidthAttributeSizeId,MetaFixedWidthAttributeSize.TYPENAME, "1.3"), fwaSize, new TypeReaderAuto(MetaFixedWidthAttributeSize.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeSize.class );	    

	    // 21 - 52. meta.fixed_width.attribute.integer
	    MetaDefinition fwaInteger = new MetaSequence( new MetaExpression[] {} );
	    library.register(new DictionaryDefinition(metaFixedWidthAttributeIntegerId,MetaFixedWidthAttributeInteger.TYPENAME, "1.3"),  fwaInteger, new TypeReaderAuto(MetaFixedWidthAttributeInteger.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeInteger.class );	    
	    
	    // 22 - 53. meta.fixed_width.attribute.unsigned
	    MetaDefinition fwaUnsigned = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(metaFixedWidthAttributeUnsignedId, MetaFixedWidthAttributeUnsigned.TYPENAME, "1.3"), fwaUnsigned, new TypeReaderAuto(MetaFixedWidthAttributeUnsigned.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeUnsigned.class );	    

	    // 23 - 54. meta.fixed_width.attribute.bigendian
	    MetaDefinition fwaBigEndian = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(metaFixedWidthAttributeBigEndianId, MetaFixedWidthAttributeBigEndian.TYPENAME, "1.3"), fwaBigEndian, new TypeReaderAuto(MetaFixedWidthAttributeBigEndian.class), new TypeBeanMarshaller(), MetaFixedWidthAttributeBigEndian.class );	    

	    // 24 - 55. dictionary.name
	    MetaDefinition dictNameDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "name", new MetaReference( metaNameId ))
	    			}
	    		);
	    library.register( new DictionaryDefinition(dictionaryNameId,DictionaryName.TYPENAME, "1.3"), dictNameDef, new TypeReaderAuto(DictionaryName.class), new TypeBeanMarshaller(), DictionaryName.class );
	    
	    // 25 - 56. dictionary.definition
	    MetaDefinition dictDefDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "name", new MetaReference( metaNameId )),
	    					new MetaTag( "version", new MetaReference( metaVersionId ))
	    			});
	   // library.register( new DictionaryDefinition(dictionaryDefinitionId, "1.3"), dictDefDef, new DictionaryDefinition.DictionaryDefinitionTypeReader(), new DictionaryDefinition.DictionaryDefinitionTypeWriter(), DictionaryDefinition.class );
	    library.register( new DictionaryDefinition(dictionaryDefinitionId,DictionaryDefinition.TYPENAME, "1.3"), dictDefDef, new TypeBeanMarshaller(), new DictionaryDefinition.DictionaryDefinitionTypeWriter(), DictionaryDefinition.class );

	    // 26 - 57. dictionary.relation
	    MetaDefinition dictRelationDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "id", new MetaReference( metaId )),
	    					new MetaTag( "tag", new MetaReference( u8utf8Id ))
	    			});
	    //library.register( new DictionaryDefinition(dictionaryRelationId, "1.3"), dictRelationDef, new DictionaryRelation.DictionaryRelationTypeReader(), new DictionaryRelation.DicitonaryRelationTypeWriter(), DictionaryRelation.class );
	    library.register( new DictionaryDefinition(dictionaryRelationId,DictionaryRelation.TYPENAME, "1.3"), dictRelationDef, new TypeBeanMarshaller(), new DictionaryRelation.DicitonaryRelationTypeWriter(), DictionaryRelation.class );

	    // 27 - 58. dictionary.location
	    MetaDefinition dictLocationDef = new MetaAbstract(
	    		new MetaAbstractMap[] {
	    				new MetaAbstractMap( dictionaryNameId ),
	    				new MetaAbstractMap( dictionaryDefinitionId ),
	    				new MetaAbstractMap( dictionaryRelationId ),
	    		});
	    library.register( new DictionaryDefinition(dictionaryLocationId,DictionaryLocation.TYPENAME, "1.3"), dictLocationDef, new MetaMarshaller(),new MetaMarshaller(), DictionaryLocation.class );

		// 28 - 59. meta.definition.envelop
		MetaDefinition dDef = new MetaEnvelop(
				new MetaReference( uint16Id ),
				new MetaReference( metaDefinitionId )
		);			  
		library.register( new DictionaryDefinition(metaDefinitionEnvelopId,"meta.definition.envelop", "1.3"), dDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// 29 - 60. dictionary.entry
		MetaDefinition entryDef =new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "id", new MetaReference( uint16Id )),
				new MetaTag( "name", new MetaReference(dictionaryLocationId)),
				new MetaTag( "definition", new MetaReference( metaDefinitionEnvelopId)) 
			}
	    );
		library.register( new DictionaryDefinition(dictionaryEntryId,"dictionary.entry", "1.3"), entryDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// 30 - 61. dictionary.entry.list
		// TODO Remove surrounding sequence.  Requires better marshaller.
		MetaDefinition dmDef = new MetaSequence( new MetaExpression[] {
				new MetaArray(
					new MetaReference(uint16Id ),
					new MetaReference(dictionaryEntryId)
				)});
		library.register( new DictionaryDefinition(dictionaryEntryListId,"dictionary.entry.list", "1.3"), dmDef, new TypeReaderAuto(TypeMap.class), new TypeMapMarshaller(), null );
		
	}

}
