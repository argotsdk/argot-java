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
 
package com.argot.common;

import java.util.Date;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.auto.TypeReaderAuto;
import com.argot.auto.TypeSimpleReader;
import com.argot.auto.TypeSimpleWriter;
import com.argot.meta.MetaIdentified;
import com.argot.meta.MetaMarshaller;


public class CommonLoader
extends ResourceDictionaryLoader
{
	public static String DICTIONARY = "common.dictionary";
	public static String VERSION = "1.3";
	
	public CommonLoader()
	{
		super(DICTIONARY);
	}
	
	public String getName()
	{
		return DICTIONARY;
	}
	
	public void bind( TypeLibrary library ) throws TypeException
	{
		int id;
		
		int typeId = library.getTypeId( Empty.TYPENAME, Empty.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			Empty te = new Empty();
			id = library.bind( typeId, new TypeSimpleReader(te),new TypeSimpleWriter(te),te.getClass() );
			library.setSimpleType(id,true);
		}
		
		// Register Big Endian Unsigned Types.
		typeId = library.getTypeId( UInt8.TYPENAME, UInt8.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt8 bbe = new UInt8();
			id = library.bind( typeId, new TypeSimpleReader(bbe),new TypeSimpleWriter(bbe),null );			
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( UInt16.TYPENAME, UInt16.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt16 bbs = new UInt16();
			id = library.bind( typeId, new TypeSimpleReader(bbs), new TypeSimpleWriter(bbs), null );
			library.setSimpleType(id,true);			
		}
		
		typeId = library.getTypeId( UInt32.TYPENAME, UInt32.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt32 bei = new UInt32();
			id = library.bind( typeId, new TypeSimpleReader(bei), new TypeSimpleWriter(bei), null );
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( UInt64.TYPENAME, UInt64.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt64 bbe = new UInt64();
			id = library.bind( typeId, new TypeSimpleReader(bbe), new TypeSimpleWriter(bbe), null );
			library.setSimpleType(id,true);
		}

		// Register Big Endian Signed Types.
		typeId = library.getTypeId( Int8.TYPENAME, Int8.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int8 bbe = new Int8();
			id = library.bind( typeId, new TypeSimpleReader(bbe), new TypeSimpleWriter(bbe),Byte.class );
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( Int16.TYPENAME, Int16.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int16 bbs = new Int16();
			id = library.bind( typeId, new TypeSimpleReader(bbs), new TypeSimpleWriter(bbs),Short.class );
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( Int32.TYPENAME, Int32.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int32 bei = new Int32();
			id = library.bind( typeId, new TypeSimpleReader(bei), new TypeSimpleWriter(bei), Integer.class );
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( Int64.TYPENAME, Int64.VERSION );
		if ( library.getTypeState(typeId) == TypeLibrary.TYPE_REGISTERED )
		{
			Int64 bbe = new Int64();
			id = library.bind( typeId, new TypeSimpleReader(bbe), new TypeSimpleWriter(bbe),Long.class );
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( IEEEFloat.TYPENAME, IEEEFloat.VERSION );
		if ( library.getTypeState(typeId) == TypeLibrary.TYPE_REGISTERED )
		{
			id = library.bind( typeId, new TypeSimpleReader(new IEEEFloat.Reader()), new TypeSimpleWriter(new IEEEFloat.Writer()), Float.class);
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( IEEEDouble.TYPENAME, IEEEDouble.VERSION );
		if ( library.getTypeState(typeId) == TypeLibrary.TYPE_REGISTERED )
		{
			id = library.bind( typeId, new TypeSimpleReader(new IEEEDouble.Reader()), new TypeSimpleWriter(new IEEEDouble.Writer()), Double.class);
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( U8Ascii.TYPENAME, U8Ascii.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U8Ascii sbu = new U8Ascii();
			id = library.bind( typeId, new TypeSimpleReader(sbu), new TypeSimpleWriter(sbu), null );
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( U32UTF8.TYPENAME, U32UTF8.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U32UTF8 sbu = new U32UTF8();
			id = library.bind( typeId, new TypeSimpleReader(sbu), new TypeSimpleWriter(sbu), null );
			library.setSimpleType(id,true);
		}

		typeId = library.getTypeId( U16ArrayByte.TYPENAME, U16ArrayByte.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			U16ArrayByte aib = new U16ArrayByte();
			id = library.bind( typeId, new TypeSimpleReader(aib), new TypeSimpleWriter(aib), byte[].class );
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( U32ArrayByte.TYPENAME, U32ArrayByte.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			U32ArrayByte asb = new U32ArrayByte();
			id = library.bind( typeId, new TypeSimpleReader(asb), new TypeSimpleWriter(asb), null );
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( U8Boolean.TYPENAME, U8Boolean.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			U8Boolean bb = new U8Boolean();
			id = library.bind( typeId, new TypeSimpleReader(bb), new TypeSimpleWriter(bb), Boolean.class );
			library.setSimpleType(id,true);
		}
		
		typeId = library.getTypeId( "meta.identified", "1.3" );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeReaderAuto(MetaIdentified.class), new MetaIdentified.MetaIdentifiedTypeWriter(), MetaIdentified.class );
		}
/*		
		typeId = library.getTypeId( "meta.optional", "1.3" );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeReaderAuto(MetaOptional.class), new MetaOptional.MetaOptionalTypeWriter(), MetaOptional.class );
		}
*/		
		typeId = library.getTypeId( "date", "1.3" );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
		    library.bind( typeId, new MetaMarshaller(), new MetaMarshaller(), null );
		}
		
		typeId = library.getTypeId( "date.java","1.3" );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
		    id = library.bind( typeId, new TypeReaderAuto( Date.class ), new DateS64(), Date.class );
			library.setSimpleType(id,true);
		}
			
	}

}
