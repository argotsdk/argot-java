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

package com.argot.data;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;


/*
 * A simple mixed data used for testing purposes.
 * 
 * mixeddata: meta.sequence([
 * 		meta.reference( #s32, "s32" );
 * 		meta.reference( #s16, "s16" );
 * 		meta.reference( #u8ascii, "u8ascii" );
 * 
 */
public class MixedData 
{
	public static final String TYPENAME = "mixeddata";
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
			out.writeObject("s32", new Integer( data._anInt ));
			out.writeObject("s16", new Short( data._aShort ));
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
	public static void register( TypeLibrary library )
	throws TypeException
	{
		library.register( MixedData.TYPENAME, 
				new MetaSequence(
					new MetaExpression[]{
					    new MetaReference( library.getId("s32"), "s32"),
					    new MetaReference( library.getId("s16"), "s16"),
					    new MetaReference( library.getId("u8ascii"), "u8ascii")
					}
				),
			new TypeReaderAuto( MixedData.class ),
			new MixedDataWriter(),
			MixedData.class
		);
	}
}
