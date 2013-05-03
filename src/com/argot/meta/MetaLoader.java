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
import com.argot.common.U8Utf8;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;
import com.argot.dictionary.Dictionary;
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
		
		// 1. baseId
		@SuppressWarnings("unused")
		int baseGroupId = library.register( new DictionaryBase( ), new MetaCluster() );
		
		// 2. uint8
		int uint8Id = library.register( new DictionaryName( library, UInt8.TYPENAME), new MetaIdentity() );  
		// 3. uvint28
		int uvint28Id = library.register( new DictionaryName( library, UVInt28.TYPENAME), new MetaIdentity() );  

		// 4. meta group
		@SuppressWarnings("unused")
		int metaClusterId = library.register( new DictionaryName(library,"meta"),  new MetaCluster() );
		
		// 5. meta.id		
		int metaId = library.register( new DictionaryName( library, "meta.id" ), new MetaIdentity() );  
		// 6. meta.cluster
		int metaClusterDefId = library.register( new DictionaryName(library, "meta.cluster"), new MetaIdentity() );
		// 7. meta.abstract.map
		int abstractMapId = library.register( new DictionaryName( library, MetaAbstractMap.TYPENAME ), new MetaIdentity() ); 
		// 8. meta.abstract
		int abstractId = library.register( new DictionaryName( library, MetaAbstract.TYPENAME ), new MetaIdentity() );  
		// 9. u8utf8
		int u8utf8Id = library.register( new DictionaryName( library, U8Utf8.TYPENAME ), new MetaIdentity() );
		//. meta.name_part
		//int metaNamePartId = library.register( new DictionaryName( library, "meta.name_part" ), new MetaIdentity() );
		// 10. meta.name
		int metaNameId = library.register( new DictionaryName( library, "meta.name" ), new MetaIdentity() );
		// 11. meta.version
		int metaVersionId = library.register( new DictionaryName( library, MetaVersion.TYPENAME ), new MetaIdentity() );
		// 12. meta.definition
		int metaDefinitionId = library.register( new DictionaryName( library, MetaDefinition.TYPENAME ), new MetaIdentity() );
		// 13. meta.expression
		int metaExpressionId = library.register( new DictionaryName( library, MetaExpression.TYPENAME ), new MetaIdentity() );
		// 14. meta.reference
		int metaReferenceId = library.register( new DictionaryName( library, MetaReference.TYPENAME ), new MetaIdentity() );
		// 15. meta.tag
		int metaTagId = library.register( new DictionaryName( library, MetaTag.TYPENAME ), new MetaIdentity() );
		// 16. meta.sequence
		int metaSequenceId = library.register( new DictionaryName( library, MetaSequence.TYPENAME ), new MetaIdentity() );
		// 17. meta.array
		int metaArrayId = library.register( new DictionaryName( library, MetaArray.TYPENAME ), new MetaIdentity() );
		// 18. meta.envelop
		int metaEnvelopId = library.register( new DictionaryName( library, MetaEnvelope.TYPENAME ), new MetaIdentity() );
		// 19. meta.encoding
		int metaEncodingId = library.register( new DictionaryName( library, MetaEncoding.TYPENAME ), new MetaIdentity() );
		// 20. meta.atom
		int metaFixedWidthId = library.register( new DictionaryName( library, MetaAtom.TYPENAME ), new MetaIdentity() );
		// 21. meta.atom_attribute
		int metaFixedWidthAttributeId = library.register( new DictionaryName( library, MetaAtomAttribute.TYPENAME ), new MetaIdentity() );
		// 22. meta.attribute cluster
		@SuppressWarnings("unused")
		int metaAtomAttributeClusterId = library.register( new DictionaryName( library, "meta.attribute"), new MetaCluster() );
		// 23. meta.attribute.size
		int metaFixedWidthAttributeSizeId = library.register( new DictionaryName( library, MetaAtomAttributeSize.TYPENAME ), new MetaIdentity() );
		// 24. meta.attribute.integer
		int metaFixedWidthAttributeIntegerId = library.register( new DictionaryName( library, MetaAtomAttributeInteger.TYPENAME ), new MetaIdentity() );
		// 25. meta.attribute.unsigned
		int metaFixedWidthAttributeUnsignedId = library.register( new DictionaryName( library, MetaAtomAttributeUnsigned.TYPENAME ), new MetaIdentity() );
		// 26. meta.attribute.bigendian
		int metaFixedWidthAttributeBigEndianId = library.register( new DictionaryName( library, MetaAtomAttributeBigEndian.TYPENAME ), new MetaIdentity() );
		// 27. dictionary cluster
		@SuppressWarnings("unused")
		int dictionaryClusterId = library.register( new DictionaryName( library, "dictionary"), new MetaCluster() );
		// 28. dictionary.base
		int dictionaryBaseId = library.register( new DictionaryName( library, DictionaryBase.TYPENAME), new MetaIdentity() );
		// 29. dictionary.name 
		int dictionaryNameId = library.register( new DictionaryName( library, DictionaryName.TYPENAME), new MetaIdentity() );
		// 30. dictionary.definition		
		int dictionaryDefinitionId = library.register( new DictionaryName( library, DictionaryDefinition.TYPENAME), new MetaIdentity() );
		// 31. dictionary.relation
		int dictionaryRelationId = library.register( new DictionaryName( library, DictionaryRelation.TYPENAME), new MetaIdentity() );
		// 32. dictionary.location
		int dictionaryLocationId = library.register( new DictionaryName( library, DictionaryLocation.TYPENAME), new MetaIdentity() );
		// 33. dictionary.definition_envelop
		int metaDefinitionEnvelopId = library.register( new DictionaryName( library, "dictionary.definition_envelope" ), new MetaIdentity() );
		// 34. dictionary.entry
		int dictionaryEntryId = library.register( new DictionaryName( library, "dictionary.entry"), new MetaIdentity() );
		// 35. dictionary.entry.list
		int dictionaryEntryListId = library.register( new DictionaryName( library, "dictionary.entry_list"), new MetaIdentity() );
		

		// 2. uint8
		UInt8 bbe = new UInt8();
		
		MetaDefinition u8def = new MetaAtom(8,8,
			new MetaAtomAttribute[] {
				new MetaAtomAttributeSize(8),
				new MetaAtomAttributeInteger(),
				new MetaAtomAttributeUnsigned(),
				new MetaAtomAttributeBigEndian()
		});

		id = library.register( new DictionaryDefinition(library, uint8Id,UInt8.TYPENAME, "1.3"), u8def, new TypeSimpleReader(bbe), new TypeSimpleWriter(bbe), null );
		library.setSimpleType(id,true);

		// 3. uvint28
		UVInt28 uvi28 = new UVInt28();
		
		MetaDefinition uvi28def = new MetaAtom(8,28,
			new MetaAtomAttribute[] {
				new MetaAtomAttributeSize(8),
				new MetaAtomAttributeInteger(),
				new MetaAtomAttributeUnsigned(),
				new MetaAtomAttributeBigEndian()
		});

		id = library.register( new DictionaryDefinition(library, uvint28Id,UVInt28.TYPENAME, "1.3"), uvi28def, new TypeSimpleReader(uvi28), new TypeSimpleWriter(uvi28), null );
		library.setSimpleType(id,true);
		
	    // 5. meta.id
	    MetaDefinition metaIdDef = new MetaReference( uvint28Id );
	    library.register( new DictionaryDefinition(library, metaId,"meta.id", "1.3"), metaIdDef, new TypeSimpleReader(new UVInt28()), new TypeSimpleWriter(new UVInt28()), null );

	    // 6. meta.cluster
	    MetaDefinition metaClusterDef = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(library, metaClusterDefId, MetaCluster.TYPENAME,"1.3" ), metaClusterDef, new TypeBeanMarshaller(), new TypeBeanMarshaller(), MetaCluster.class );

	    // 7. meta.abstract_map 
		MetaDefinition metaMapDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "id", new MetaReference( metaId)),
				});
		library.register( new DictionaryDefinition(library, abstractMapId,MetaAbstractMap.TYPENAME, "1.3"), metaMapDef, new TypeSimpleReader(new MetaAbstractMap.MetaMapTypeReader()), new TypeSimpleWriter(new MetaAbstractMap.MetaMapTypeWriter()), MetaAbstractMap.class );

		// 8. meta.abstract
		MetaDefinition abstractDef = new MetaSequence(
				new MetaExpression[] {
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( abstractMapId )
					)	
				});
		library.register( new DictionaryDefinition(library, abstractId,MetaAbstract.TYPENAME, "1.3"), abstractDef, (TypeLibraryReader) new MetaAbstract.MetaAbstractTypeReader(), (TypeLibraryWriter) new MetaAbstract.MetaAbstractTypeWriter(), MetaAbstract.class );
	    

		// 9. u8ascii
		U8Utf8 u8utf8 = new U8Utf8();
		MetaDefinition u8asciiDef = 
			new MetaEncoding(
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( uint8Id )
					),
					"UTF-8"
				);
		id = library.register(new DictionaryDefinition(library, u8utf8Id,U8Utf8.TYPENAME, "1.3"), u8asciiDef, new TypeSimpleReader(u8utf8), new TypeSimpleWriter(u8utf8), String.class );
		library.setSimpleType(id,true);
		
	
		// 10. meta.name
		MetaDefinition nameDef = 
			new MetaSequence(
				new MetaExpression[] {
						new MetaTag( "group", new MetaReference( metaId )),
						new MetaTag( "name", new MetaReference( u8utf8Id ))
				});
		
		library.register(new DictionaryDefinition( library, metaNameId,"meta.name", "1.3"), nameDef, new MetaName.MetaNameTypeLibraryReader(),new MetaName.MetaNameTypeWriter(), MetaName.class );

		// 11. meta.version
		MetaDefinition versionDef = 
			new MetaSequence(
					new MetaExpression[] {
							new MetaTag( "major", new MetaReference( uint8Id )),
							new MetaTag( "minor", new MetaReference( uint8Id ))
					}
			);
		library.register(new DictionaryDefinition(library, metaVersionId,MetaVersion.TYPENAME, "1.3"), versionDef, new TypeBeanMarshaller(),new TypeBeanMarshaller(), MetaVersion.class );


		// 13. meta.expression
		MetaDefinition exprDef = new MetaAbstract(
				new MetaAbstractMap[] {
						new MetaAbstractMap( metaReferenceId ),
						new MetaAbstractMap( metaTagId ),
						new MetaAbstractMap( metaSequenceId ),
						new MetaAbstractMap( metaArrayId ), 
						new MetaAbstractMap( metaEnvelopId ),
						new MetaAbstractMap( metaEncodingId )
				});		
		int exprDefId = library.register(new DictionaryDefinition( library, metaExpressionId,MetaExpression.TYPENAME, "1.3"), exprDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
		// 12. meta.definition
		MetaDefinition defDef = new MetaAbstract(
				new MetaAbstractMap[] {
						new MetaAbstractMap( metaClusterDefId ),
						new MetaAbstractMap( metaFixedWidthId ),
						new MetaAbstractMap( abstractId ),
						new MetaAbstractMap( abstractMapId ),
						new MetaAbstractMap( exprDefId )
				/*		new MetaAbstractMap( metaIdentityId ) */
				});		
		library.register(new DictionaryDefinition( library, metaDefinitionId,MetaDefinition.TYPENAME, "1.3"), defDef, new MetaMarshaller(),new MetaMarshaller(), null );
		
				
		// 14. meta.reference
		MetaDefinition refDef = new MetaSequence( 
				new MetaExpression[] { 
						new MetaReference( metaId )});
		library.register( new DictionaryDefinition( library, metaReferenceId, MetaReference.TYPENAME,"1.3"), refDef, (TypeLibraryReader) new MetaReference.MetaReferenceTypeReader(), (TypeLibraryWriter) new MetaReference.MetaReferenceTypeWriter(), MetaReference.class );
		
		// 15. meta.tag
		MetaDefinition tagDef = new MetaSequence( new MetaExpression[] { 
				new MetaTag( "name", new MetaReference( u8utf8Id )),
				new MetaTag( "data", new MetaReference( metaExpressionId ))
		});
		library.register(new DictionaryDefinition( library,metaTagId,MetaTag.TYPENAME, "1.3"), tagDef, new MetaTag.MetaTagTypeReader(), new MetaTag.MetaTagTypeWriter(), MetaTag.class );
		
		// 16. meta.sequence
		// TODO Remove MetaSequence.  Requires better Marshaller classes.
		MetaDefinition seqDef = new MetaSequence( new MetaExpression[] { new MetaArray(
					new MetaReference( uint8Id),
					new MetaReference( metaExpressionId))});
		library.register( new DictionaryDefinition( library, metaSequenceId,MetaSequence.TYPENAME, "1.3"), seqDef, new MetaSequence.MetaSequenceTypeReader(), new MetaSequence.MetaSequenceTypeWriter(), MetaSequence.class );
			
		// 17. meta.array
		MetaDefinition arrayDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "size", new MetaReference( metaExpressionId)),
				new MetaTag( "type", new MetaReference( metaExpressionId))
			}
		);
		library.register( new DictionaryDefinition( library, metaArrayId,MetaArray.TYPENAME, "1.3"), arrayDef, new MetaArray.MetaArrayTypeReader(), new MetaArray.MetaArrayTypeWriter(), MetaArray.class );

		// 18. meta.envelope
		MetaDefinition meDef = new MetaSequence(
				new MetaExpression[] {
					new MetaTag( "size", new MetaReference( metaExpressionId)),
					new MetaTag( "type",new MetaReference( metaExpressionId))
				}
			);
		library.register(new DictionaryDefinition( library, metaEnvelopId,MetaEnvelope.TYPENAME, "1.3"), meDef, new MetaEnvelope.MetaEnvelopTypeReader(), new MetaEnvelope.MetaEnvelopTypeWriter(), MetaEnvelope.class );
		
		// 19. meta.encoding
		MetaDefinition encodingDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "data", new MetaReference( metaExpressionId)),
				new MetaTag( "encoding", new MetaReference( u8utf8Id ))
			}
		);
		library.register( new DictionaryDefinition( library,metaEncodingId, MetaEncoding.TYPENAME, "1.3"), encodingDef, new MetaEncoding.MetaEncodingTypeReader(), new MetaEncoding.MetaEncodingTypeWriter(), MetaEncoding.class );

	    // 20. meta.atom
		MetaDefinition basicDef = new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "min_bit_length", new MetaReference( uvint28Id )),
				new MetaTag( "max_bit_length", new MetaReference( uvint28Id )),
				new MetaTag( "attributes",
					new MetaArray(
						new MetaReference( uint8Id ),
						new MetaReference( metaFixedWidthAttributeId )
					)
				)
			}
		);	
		library.register( new DictionaryDefinition( library,metaFixedWidthId, MetaAtom.TYPENAME, "1.3"), basicDef, new TypeReaderAuto(MetaAtom.class), new MetaAtom.MetaBasicTypeWriter(), MetaAtom.class );

	    // 21. meta.atom_attribute 
	    MetaDefinition fwAttribute = new MetaAbstract(
	    		new MetaAbstractMap[] {
	    				new MetaAbstractMap( metaFixedWidthAttributeSizeId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeIntegerId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeUnsignedId ),
	    				new MetaAbstractMap( metaFixedWidthAttributeBigEndianId )
	    		});
	    library.register( new DictionaryDefinition( library,metaFixedWidthAttributeId,MetaAtomAttribute.TYPENAME, "1.3"), fwAttribute, new MetaMarshaller(),new MetaMarshaller(), null );
		
	    // 23. meta.attribute.size
	    MetaDefinition fwaSize = new MetaSequence( new MetaExpression[] { new MetaTag( "size", new MetaReference( uvint28Id )) });
	    library.register( new DictionaryDefinition( library,metaFixedWidthAttributeSizeId,MetaAtomAttributeSize.TYPENAME, "1.3"), fwaSize, new TypeReaderAuto(MetaAtomAttributeSize.class), new TypeBeanMarshaller(), MetaAtomAttributeSize.class );	    

	    // 24. meta.attribute.integer
	    MetaDefinition fwaInteger = new MetaSequence( new MetaExpression[] {} );
	    library.register(new DictionaryDefinition(library,metaFixedWidthAttributeIntegerId,MetaAtomAttributeInteger.TYPENAME, "1.3"),  fwaInteger, new TypeReaderAuto(MetaAtomAttributeInteger.class), new TypeBeanMarshaller(), MetaAtomAttributeInteger.class );	    
	    
	    // 25. meta.attribute.unsigned
	    MetaDefinition fwaUnsigned = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(library,metaFixedWidthAttributeUnsignedId, MetaAtomAttributeUnsigned.TYPENAME, "1.3"), fwaUnsigned, new TypeReaderAuto(MetaAtomAttributeUnsigned.class), new TypeBeanMarshaller(), MetaAtomAttributeUnsigned.class );	    

	    // 26. meta.attribute.bigendian
	    MetaDefinition fwaBigEndian = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(library,metaFixedWidthAttributeBigEndianId, MetaAtomAttributeBigEndian.TYPENAME, "1.3"), fwaBigEndian, new TypeReaderAuto(MetaAtomAttributeBigEndian.class), new TypeBeanMarshaller(), MetaAtomAttributeBigEndian.class );	    

	    // 28. dictionary.base
	    MetaDefinition dictBaseDef = new MetaSequence( new MetaExpression[] {} );
	    library.register( new DictionaryDefinition(library,dictionaryBaseId,DictionaryBase.TYPENAME,VERSION), dictBaseDef, new TypeBeanMarshaller(), new TypeBeanMarshaller(), DictionaryBase.class );
	    
	    // 29. dictionary.name
	    MetaDefinition dictNameDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "name", new MetaReference( metaNameId ))
	    			}
	    		);
	    library.register( new DictionaryDefinition(library,dictionaryNameId,DictionaryName.TYPENAME, "1.3"), dictNameDef, new TypeReaderAuto(DictionaryName.class), new TypeBeanMarshaller(), DictionaryName.class );

	    // 30. dictionary.definition
	    MetaDefinition dictDefDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "name", new MetaReference( metaNameId )),
	    					new MetaTag( "version", new MetaReference( metaVersionId ))
	    			});
	    library.register( new DictionaryDefinition(library,dictionaryDefinitionId,DictionaryDefinition.TYPENAME, "1.3"), dictDefDef, new TypeBeanMarshaller(), new DictionaryDefinition.DictionaryDefinitionTypeWriter(), DictionaryDefinition.class );

	    // 31. dictionary.relation
	    MetaDefinition dictRelationDef = new MetaSequence(
	    			new MetaExpression[] {
	    					new MetaTag( "id", new MetaReference( metaId )),
	    					new MetaTag( "tag", new MetaReference( u8utf8Id ))
	    			});
	    library.register( new DictionaryDefinition(library,dictionaryRelationId,DictionaryRelation.TYPENAME, "1.3"), dictRelationDef, new TypeBeanMarshaller(), new DictionaryRelation.DicitonaryRelationTypeWriter(), DictionaryRelation.class );

	    // 32. dictionary.location
	    MetaDefinition dictLocationDef = new MetaAbstract(
	    		new MetaAbstractMap[] {
	    				new MetaAbstractMap( dictionaryBaseId ),
	    				new MetaAbstractMap( dictionaryNameId ),
	    				new MetaAbstractMap( dictionaryDefinitionId ),
	    				new MetaAbstractMap( dictionaryRelationId ),
	    		});
	    library.register( new DictionaryDefinition(library,dictionaryLocationId,DictionaryLocation.TYPENAME, "1.3"), dictLocationDef, new MetaMarshaller(),new MetaMarshaller(), DictionaryLocation.class );

		// 33. dictionary.definition_envelop
		MetaDefinition dDef = new MetaEnvelope(
				new MetaReference( uvint28Id ),
				new MetaReference( metaDefinitionId )
		);			  
		library.register( new DictionaryDefinition(library,metaDefinitionEnvelopId,MetaDefinition.META_DEFINITION_ENVELOPE, "1.3"), dDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// 34. dictionary.entry
		MetaDefinition entryDef =new MetaSequence(
			new MetaExpression[] {
				new MetaTag( "id", new MetaReference( uvint28Id )),
				new MetaTag( "name", new MetaReference(dictionaryLocationId)),
				new MetaTag( "definition", new MetaReference( metaDefinitionEnvelopId)) 
			}
	    );
		library.register( new DictionaryDefinition(library,dictionaryEntryId,Dictionary.DICTIONARY_ENTRY, Dictionary.DICTIONARY_ENTRY_VERSION), entryDef, new MetaMarshaller(), new MetaMarshaller(), null );
		
		// 35. dictionary.entry.list
		// TODO Remove surrounding sequence.  Requires better marshaller.
		MetaDefinition dmDef = new MetaSequence( new MetaExpression[] {
				new MetaArray(
					new MetaReference(uvint28Id ),
					new MetaReference(dictionaryEntryId)
				)});
		library.register( new DictionaryDefinition(library,dictionaryEntryListId,Dictionary.DICTIONARY_ENTRY_LIST, Dictionary.DICTIONARY_ENTRY_LIST_VERSION), dmDef, new TypeReaderAuto(TypeMap.class), new TypeMapMarshaller(), null );
		
		
		library.setPrimed();
	}

}
