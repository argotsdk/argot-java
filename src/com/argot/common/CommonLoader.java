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
 
package com.argot.common;

import java.util.Date;

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeReaderAuto;
import com.argot.meta.MetaIdentified;
import com.argot.meta.MetaMarshaller;
import com.argot.meta.MetaOptional;

public class CommonLoader
extends ResourceDictionaryLoader
{
	public static String DICTIONARY = "common.dictionary";
	
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
		
		if ( library.getTypeState( Empty.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Empty te = new Empty();
			id = library.bind( Empty.TYPENAME,te,te,te.getClass() );
			library.setSimpleType(id,true);
		}
		
		// Register Big Endian Unsigned Types.
		if ( library.getTypeState( UInt8.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt8 bbe = new UInt8();
			id = library.bind( UInt8.TYPENAME, bbe,bbe,null );			
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( UInt16.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt16 bbs = new UInt16();
			id = library.bind( UInt16.TYPENAME, bbs,bbs, null );
			library.setSimpleType(id,true);			
		}
		
		if ( library.getTypeState( UInt32.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt32 bei = new UInt32();
			id = library.bind( UInt32.TYPENAME, bei,bei, null );
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState( UInt64.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			UInt64 bbe = new UInt64();
			id = library.bind( UInt64.TYPENAME, bbe,bbe, null );
			library.setSimpleType(id,true);
		}

		// Register Big Endian Signed Types.
		if ( library.getTypeState( Int8.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int8 bbe = new Int8();
			id = library.bind( Int8.TYPENAME, bbe,bbe,Byte.class );
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( Int16.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int16 bbs = new Int16();
			id = library.bind( Int16.TYPENAME, bbs,bbs,Short.class );
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( Int32.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int32 bei = new Int32();
			id = library.bind( Int32.TYPENAME, bei,bei, Integer.class );
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState(Int64.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Int64 bbe = new Int64();
			id = library.bind( Int64.TYPENAME, bbe,bbe,Long.class );
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState(IEEEFloat.TYPENAME) == TypeLibrary.TYPE_REGISTERED )
		{
			id = library.bind( IEEEFloat.TYPENAME, new IEEEFloat.Reader(), new IEEEFloat.Writer(), Float.class);
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState(IEEEDouble.TYPENAME) == TypeLibrary.TYPE_REGISTERED )
		{
			id = library.bind( IEEEDouble.TYPENAME, new IEEEDouble.Reader(), new IEEEDouble.Writer(), Double.class);
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( U8Ascii.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U8Ascii sbu = new U8Ascii();
			id = library.bind( U8Ascii.TYPENAME, sbu, sbu, String.class );
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState( U32UTF8.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U32UTF8 sbu = new U32UTF8();
			id = library.bind( U32UTF8.TYPENAME, sbu, sbu, null );
			library.setSimpleType(id,true);
		}

		if ( library.getTypeState( U16ArrayByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U16ArrayByte aib = new U16ArrayByte();
			id = library.bind( U16ArrayByte.TYPENAME, aib,aib, byte[].class );
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( U32ArrayByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U32ArrayByte asb = new U32ArrayByte();
			id = library.bind( U32ArrayByte.TYPENAME, asb, asb, null );
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( U8Boolean.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U8Boolean bb = new U8Boolean();
			id = library.bind( U8Boolean.TYPENAME, bb, bb, Boolean.class );
			library.setSimpleType(id,true);
		}
		
		if ( library.getTypeState( "meta.identified" ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaIdentified.TYPENAME, new TypeReaderAuto(MetaIdentified.class), new MetaIdentified.MetaIdentifiedTypeWriter(), MetaIdentified.class );
		}
		
		if ( library.getTypeState( "meta.optional" ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaOptional.TYPENAME, new TypeReaderAuto(MetaOptional.class), new MetaOptional.MetaOptionalTypeWriter(), MetaOptional.class );
		}
		
		if ( library.getTypeState( "date" ) == TypeLibrary.TYPE_REGISTERED )
		{
		    library.bind( "date", new MetaMarshaller(), new MetaMarshaller(), null );
		}
		if ( library.getTypeState( "date.java") == TypeLibrary.TYPE_REGISTERED )
		{
		    id = library.bind( DateS64.TYPENAME, new TypeReaderAuto( Date.class ), new DateS64(), Date.class );
			library.setSimpleType(id,true);
		}
			
	}

}
