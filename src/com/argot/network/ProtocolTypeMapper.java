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
package com.argot.network;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeMapper;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaName;
import com.argot.util.ChunkByteBuffer;

public class ProtocolTypeMapper
implements TypeMapper
{
	public static final byte MAP = 1;
	public static final byte MAPDEF = 2;
	public static final byte MAPRES = 3;
	public static final byte MAPREV = 4;
	public static final byte BASE = 5;
	public static final byte ERROR = 6;
	public static final byte MSG = 7;
	public static final byte CHECK_CORE = 8;

	
	private TypeLibrary _library;
	
	public ProtocolTypeMapper()
	throws TypeException
	{
	}

	public void initialise(TypeMap map) 
	throws TypeException 
	{
		TypeLibrary library = map.getLibrary();
		_library = library;
		
		map.map( 1, library.getDefinitionId( "uint8", "1.3" ));
		map.map( 2, library.getDefinitionId( "u8utf8", "1.3" ));
		map.map( 3, library.getDefinitionId( "int32", "1.3" ));
		map.map( 4, library.getDefinitionId( "u16binary","1.3" ));
		map.map( 5, library.getDefinitionId( "uint16", "1.3" ));
		map.map( 6, library.getDefinitionId( "uint32", "1.3" ));
		map.map( 7, library.getDefinitionId( "u32binary", "1.3" ));
		map.setReader( 7, new ChunkByteBuffer.ChunkByteBufferReader() );
		map.setWriter( 7, new ChunkByteBuffer.ChunkByteBufferWriter() );
		map.map( 8, library.getDefinitionId( "bool" , "1.3"));
		map.map( 9, library.getDefinitionId( DictionaryLocation.TYPENAME, "1.3" ));
		map.map( 10, library.getDefinitionId( DictionaryName.TYPENAME, "1.3" ));
		map.map( 11, library.getDefinitionId( DictionaryDefinition.TYPENAME, "1.3" ));
		map.map( 12, library.getDefinitionId( DictionaryRelation.TYPENAME, "1.3" ));
		map.map( 13, library.getDefinitionId( "meta.abstract", "1.3"));
		map.map( 14, library.getDefinitionId( "meta.id", "1.3"));
		map.map( 16, library.getDefinitionId( "meta.name", "1.3"));
		map.map( 17, library.getDefinitionId( "meta.version", "1.3"));
		map.map( 18, library.getDefinitionId( "meta.tag", "1.3"));
		map.map( 19, library.getDefinitionId( "meta.reference", "1.3"));
		map.map( 20, library.getDefinitionId( "meta.sequence", "1.3"));
		map.map( 21, library.getDefinitionId( "meta.array", "1.3"));
		map.map( 22, library.getDefinitionId( "uvint28", "1.3"));
		
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		MetaName name = _library.getName(definitionId);
		throw new TypeException("type not mapped:" + name );
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		throw new TypeException("type not mapped");
	}

	public int map(TypeLocation location) 
	throws TypeException 
	{
		throw new TypeException("type not mapped");
	}

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		throw new TypeException("type not mapped");
	}
}
