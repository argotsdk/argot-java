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
package com.argot.network;

import com.argot.TypeException;
import com.argot.TypeMap;
import com.argot.TypeLibrary;
import com.argot.util.ChunkByteBuffer;

public class ProtocolTypeMap
extends TypeMap
{
	public static final byte MAP = 1;
	public static final byte MAPRES = 2;
	public static final byte MAPREV = 3;
	public static final byte BASE = 4;
	public static final byte ERROR = 5;
	public static final byte MSG = 6;
	public static final byte CHECK_CORE = 7;

	public ProtocolTypeMap( TypeLibrary library )
	throws TypeException
	{
		super( library );
		
		map( 1, library.getId( "uint8" ));
		map( 2, library.getId( "u8ascii" ));
		map( 3, library.getId( "sint32" ));
		map( 4, library.getId( "u16binary" ));
		map( 5, library.getId( "uint16" ));
		map( 6, library.getId( "uint32" ));
		map( 7, library.getId( "u32binary" ));
		this.setReader( 7, new ChunkByteBuffer.ChunkByteBufferReader() );
		this.setWriter( 7, new ChunkByteBuffer.ChunkByteBufferWriter() );
		map( 8, library.getId( "bool" ));
	}
}
