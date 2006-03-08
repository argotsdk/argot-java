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
package com.argot;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Iterator;

import com.argot.common.DateS64;
import com.argot.common.U16ArrayByte;
import com.argot.common.U32ArrayByte;
import com.argot.common.BigEndianSignedByte;
import com.argot.common.BigEndianSignedInteger;
import com.argot.common.BigEndianSignedLong;
import com.argot.common.BigEndianSignedShort;
import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.BigEndianUnsignedInteger;
import com.argot.common.BigEndianUnsignedLong;
import com.argot.common.BigEndianUnsignedShort;
import com.argot.common.U8Boolean;
import com.argot.common.U8Ascii;
import com.argot.common.U32UTF8;
import com.argot.meta.MetaDefinition;
import com.argot.util.TwoWayHashMap;

/**
 * A TypeMap is used to map an external systems type identifiers to
 * the internal TypeSystemLibrary of the host system.
 */
public class TypeMap
{
    public static int NOTYPE = -1;
    
	TypeLibrary _library;
	TwoWayHashMap _map;

	public int size()
	{
		return _map.size();
	}
	
	public Iterator getIterator()
	{
		return _map.iterator();
	}
	
	public TypeMap( TypeLibrary library )
	{
		_library = library;
		_map = new TwoWayHashMap();
	}
	
	public int mapCommon( int base )
	throws TypeException
	{
		// Map Unsigned types.
		if ( !isValid( BigEndianUnsignedByte.TYPENAME ) )
			map( base+1, getLibrary().getId( BigEndianUnsignedByte.TYPENAME ) );
		
		if ( !isValid( BigEndianUnsignedShort.TYPENAME ) )
			map( base+2, getLibrary().getId( BigEndianUnsignedShort.TYPENAME ) );
			
		if ( !isValid( BigEndianUnsignedInteger.TYPENAME ) )
			map( base+3, getLibrary().getId( BigEndianUnsignedInteger.TYPENAME ));
					
		if ( !isValid( BigEndianUnsignedLong.TYPENAME ) )
			map( base+4, getLibrary().getId( BigEndianUnsignedLong.TYPENAME ));
			
		// Map Signed types.
		if ( !isValid( BigEndianSignedByte.TYPENAME ) )
			map( base+5, getLibrary().getId( BigEndianSignedByte.TYPENAME ) );

		if ( !isValid( BigEndianSignedShort.TYPENAME ) )
			map( base+6, getLibrary().getId( BigEndianSignedShort.TYPENAME ) );
			
		if ( !isValid( BigEndianSignedInteger.TYPENAME ) )
			map( base+7, getLibrary().getId( BigEndianSignedInteger.TYPENAME ));
					
		if ( !isValid( BigEndianSignedLong.TYPENAME ) )
			map( base+8, getLibrary().getId( BigEndianSignedLong.TYPENAME ));
	
		if ( !isValid( U8Ascii.TYPENAME ) )
			map( base+9, getLibrary().getId( U8Ascii.TYPENAME ));
								
		if ( !isValid( U32UTF8.TYPENAME ) )
			map( base+10, getLibrary().getId( U32UTF8.TYPENAME ));
			
		if ( !isValid( U16ArrayByte.TYPENAME ) )
			map( base+11, getLibrary().getId( U16ArrayByte.TYPENAME ));

		if ( !isValid( U32ArrayByte.TYPENAME ) )
			map( base+12, getLibrary().getId( U32ArrayByte.TYPENAME ));
			
		if ( !isValid( U8Boolean.TYPENAME ) )
			map( base+13, getLibrary().getId( U8Boolean.TYPENAME ));
			
		if ( !isValid( DateS64.TYPENAME ))
		    map ( base+14, getLibrary().getId( DateS64.TYPENAME ));
		
		return base+14;
	}
	
	public void map( int id, int systemId )
	throws TypeException
	{
		int i;
		
		if ( systemId == NOTYPE  )
			throw new TypeException( "typeid not valid" + systemId );

		// check if it hasn't already been mapped.
		if ( (i = _map.findValue( id )) != -1 )
		{
			if ( i != systemId )
				throw new TypeException("id already mapped to different type");

			// already mapped to the same value.
			return;
		}
	
		TypeMapItem item = new TypeMapItem();
		//item.reader = _library.getReader( systemId );
		//item.writer = _library.getWriter( systemId );
		_map.add( id, systemId, item );
	}
	
	/**
	 * This checks if the id, name & structure are the same as used
	 * in the map.  The byte[] must follow the TypeMapCore type id's.
	 * Any function or references must be valid in the context of this
	 * TypeMap.
	 * 
	 * This is like the register version below, however in some cases
	 * like a protocol you just need to check if the id's are the same.
	 * 
	 * 
	 * @param id
	 * @param name
	 * @param structure
	 * @return
	 * @throws TypeException
	 */
	public boolean isSame( int id, String name, byte[] structure, ReferenceTypeMap coreMap )
	{
		// First check if we can find the same identifier.
		int i;
		
		try 
		{
			i = _library.getId(name);
		}
		catch (TypeException e1) 
		{
			return false;
		}
			
		// Are the identifiers the same.
		if ( id != i )
		{
			return false;
		}

		// Are the definitions the same.
		try
		{
			// read the definition.
			coreMap.setReferenceMap(this);
			TypeElement definition = readStructure( coreMap, structure );
			
			// check what we've read with the local version.
			TypeElement localStruct = _library.getStructure( i );
			return TypeHelper.structureMatches( coreMap, definition, localStruct );
		}
		catch (TypeException e)
		{
			// Any errors, must not match.
			return false;
		}
	}

	public TypeElement readStructure( ReferenceTypeMap core, byte[] structure )
	throws TypeException
	{
		ByteArrayInputStream bais = new ByteArrayInputStream( structure );
		TypeInputStream tmis = new TypeInputStream( bais, core);
		
		try
		{
			Object definition = tmis.readObject( MetaDefinition.TYPENAME );
			return (TypeElement) definition;
		}
		catch (IOException e)
		{
			throw new TypeException( "failed reading structure:" + e.getMessage() );
		}
		
	}

	public int getId(String name)
	throws TypeException
	{
		int i = _library.getId(name);
		
		i = _map.findKey(i);
		if (i == -1)
			throw new TypeException("not mapped");
		
		return i;
	}


	public String getName(int id) throws TypeException
	{
		return _library.getName( getSystemId( id ) );
	}

	public void setReader( int id, TypeReader reader ) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		item.reader = reader;
	}
	
	public void setWriter( int id, TypeWriter writer ) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );		
		item.writer = writer;
	}	
	
	public TypeReader getReader(int id) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		if ( item == null )
			return _library.getReader( getSystemId( id ) );
		
		if ( item.reader == null )
			item.reader = _library.getReader( getSystemId( id ));
		
		return item.reader;
	}

	public TypeWriter getWriter(int id) throws TypeException
	{	
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		if ( item == null )
			return _library.getWriter( getSystemId( id ) );
		
		if ( item.writer == null )
			item.writer = _library.getWriter( getSystemId( id ));		
		return item.writer;
	}
	
	public TypeElement getStructure(int id) throws TypeException
	{
		return _library.getStructure( getSystemId(id) );
	}

	public Class getClass( int id )
	throws TypeException
	{
		return _library.getClass( getSystemId(id ));
	}
	
	public int getId( Class clss) 
	throws TypeException
	{
		int id = _library.getId(clss);

		id = _map.findKey(id);
		if (id == -1)
			throw new TypeException("not mapped");
		
		return id;	
	}

	public int getSystemId( int id )
	throws TypeException
	{
		int i = _map.findValue(id);
		if (i == -1)
			throw new TypeException("not found");

		return i;	
	}

	public int getId( int systemid )
	throws TypeException
	{
		int id = _map.findKey(systemid);
		if (id == -1)
		{
			String name = "invalid type";
			try
			{
				name = _library.getName(systemid);				
			}
			catch (TypeException e)
			{	
				// ignore.
			}

			throw new TypeException("not mapped: " + name );			
		}
		return id;
	}

	public TypeLibrary getLibrary()
	{
		return _library;
	}
	
	public boolean isValid( int id )
	{
		int i = _map.findValue(id);
		if (i == -1)
			return false;
		
		return true;
	}
	
	public boolean isValid( String name )
	{
		int i;
		
		try
		{
			i = _library.getId(name);
		}
		catch (TypeException ex )
		{
			return false;
		}
		
		i = _map.findKey(i);
		if (i == -1)
			return false;
		
		return true;	
	}
	
	public class TypeMapItem
	{
		public TypeReader reader;
		public TypeWriter writer;
	}
}
