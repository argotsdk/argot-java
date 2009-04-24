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

package com.argot.data;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;


/*
 * A simple mixed data used for testing purposes.
 * 
 * mixeddata: meta.sequence([
 * 		meta.tag( "int32", meta.reference( #s32 ));
 * 		meta.tag( "int16", meta.reference( #s16 ));
 * 		meta.tag( "u8ascii", meta.reference( #u8ascii ));
 * 
 */
public class MixedData 
{
	public static final String TYPENAME = "mixeddata";
	public static final String VERSION = "1.0";
	
	private int _anInt;
	private short _aShort;
	private String _anAscii;
	
	public MixedData( int anInt, short aShort, String anAscii )
	{
		_anInt = anInt;
		_aShort = aShort;
		_anAscii = anAscii;
	}
	
	public int getInt()
	{
		return _anInt;
	}
	
	public short getShort()
	{
		return _aShort;
	}
	
	public String getString()
	{
		return _anAscii;
	}
	
	public static class MixedDataWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MixedData data = (MixedData) o;
			out.writeObject("uint16", new Integer( data._anInt ));
			out.writeObject("uint8", new Short( data._aShort ));
			out.writeObject("u8ascii", data._anAscii );
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}		
	}

	/*
	 * This should be contained in a dictionary file instead
	 * of being created in code.  Useful here for testing.
	 */
	public static int register( TypeLibrary library )
	throws TypeException
	{
		int id = library.register( new DictionaryName(TYPENAME), new MetaIdentity() );
		
		return library.register( 
				new DictionaryDefinition(id, TYPENAME,"1.0"),
				new MetaSequence(
					new MetaExpression[]{
					    new MetaTag( "uint16", new MetaReference( library.getTypeId("uint16"))),
					    new MetaTag( "uint8", new MetaReference( library.getTypeId("uint8"))),
					    new MetaTag( "u8ascii", new MetaReference( library.getTypeId("u8ascii")))
					}
				),
			new TypeReaderAuto( MixedData.class ),
			new MixedDataWriter(),
			MixedData.class
		);
	}
}
