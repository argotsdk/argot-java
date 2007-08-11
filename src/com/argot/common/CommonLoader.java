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
		if ( library.getTypeState( Empty.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			Empty te = new Empty();
			library.bind( Empty.TYPENAME,te,te,te.getClass() );
		}
		
		// Register Big Endian Unsigned Types.
		if ( library.getTypeState( BigEndianUnsignedByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianUnsignedByte bbe = new BigEndianUnsignedByte();
			library.bind( BigEndianUnsignedByte.TYPENAME, bbe,bbe,null );			
		}
		
		if ( library.getTypeState( BigEndianUnsignedShort.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianUnsignedShort bbs = new BigEndianUnsignedShort();
			library.bind( BigEndianUnsignedShort.TYPENAME, bbs,bbs, null );
		}
		
		if ( library.getTypeState( BigEndianUnsignedInteger.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianUnsignedInteger bei = new BigEndianUnsignedInteger();
			library.bind( BigEndianUnsignedInteger.TYPENAME, bei,bei, null );
		}

		if ( library.getTypeState( BigEndianUnsignedLong.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianUnsignedLong bbe = new BigEndianUnsignedLong();
			library.bind( BigEndianUnsignedLong.TYPENAME, bbe,bbe, null );			
		}

		// Register Big Endian Signed Types.
		if ( library.getTypeState( BigEndianSignedByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianSignedByte bbe = new BigEndianSignedByte();
			library.bind( BigEndianSignedByte.TYPENAME, bbe,bbe,Byte.class );			
		}
		
		if ( library.getTypeState( BigEndianSignedShort.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianSignedShort bbs = new BigEndianSignedShort();
			library.bind( BigEndianSignedShort.TYPENAME, bbs,bbs,Short.class );
		}
		
		if ( library.getTypeState( BigEndianSignedInteger.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianSignedInteger bei = new BigEndianSignedInteger();
			library.bind( BigEndianSignedInteger.TYPENAME, bei,bei, Integer.class );
		}

		if ( library.getTypeState(BigEndianSignedLong.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			BigEndianSignedLong bbe = new BigEndianSignedLong();
			library.bind( BigEndianSignedLong.TYPENAME, bbe,bbe,Long.class );			
		}

		if ( library.getTypeState( U8Ascii.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U8Ascii sbu = new U8Ascii();
			library.bind( U8Ascii.TYPENAME, sbu, sbu, String.class );
		}

		if ( library.getTypeState( U32UTF8.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{	
			U32UTF8 sbu = new U32UTF8();
			library.bind( U32UTF8.TYPENAME, sbu, sbu, null );
		}

		if ( library.getTypeState( U16ArrayByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U16ArrayByte aib = new U16ArrayByte();
			library.bind( U16ArrayByte.TYPENAME, aib,aib, byte[].class );
		}
		
		if ( library.getTypeState( U32ArrayByte.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U32ArrayByte asb = new U32ArrayByte();
			library.bind( U32ArrayByte.TYPENAME, asb, asb, null );
		}
		
		if ( library.getTypeState( U8Boolean.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			U8Boolean bb = new U8Boolean();
			library.bind( U8Boolean.TYPENAME, bb, bb, Boolean.class );
		}
		
		if ( library.getTypeState( "meta.identified" ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaIdentified.TYPENAME, new MetaIdentified.MetaIdentifiedTypeReader(), new MetaIdentified.MetaIdentifiedTypeWriter(), MetaIdentified.class );
		}
		
		if ( library.getTypeState( "date" ) == TypeLibrary.TYPE_REGISTERED )
		{
		    library.bind( "date", new MetaMarshaller(), new MetaMarshaller(), null );
		}
		if ( library.getTypeState( "date.java") == TypeLibrary.TYPE_REGISTERED )
		{
		    library.bind( DateS64.TYPENAME, new TypeReaderAuto( Date.class ), new DateS64(), Date.class );
		}
			
	}

}
