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
import java.util.ArrayList;
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

/**
 * A TypeMap is used to map an external systems type identifiers to
 * the internal TypeSystemLibrary of the host system.
 */
public class TypeMap
{
    public static int NOTYPE = -1;
    
	TypeLibrary _library;
	ArrayList _mapForward;
	ArrayList _mapBack;
	ArrayList _readers;
	
	public class TypeMapIterator
	implements Iterator
	{
		private int _id;
		
		public TypeMapIterator()
		{
			_id = -1;
		}
		
		public boolean hasNext() 
		{
			int i = _id+1;
			
			while ( _mapForward.size() > i )
			{
				Integer x = (Integer) _mapForward.get( i );
				if ( x.intValue() != -1 )
					return true;
				i++;
			}	
			return false;
		}

		public Object next() 
		{
			while ( _mapForward.size() > _id )
			{
				_id++;
				Integer i = (Integer) _mapForward.get( _id );
				if ( i.intValue() != -1 )
					return new Integer( _id );
			}

			return null;
		}

		public void remove() 
		{
			// DO NOTHING.
		}
	}
	
	public int size()
	{
		int i = 0;
		for ( int x=0; x< _mapForward.size();x++ )
		{
			if ( ((Integer)_mapForward.get(x)).intValue() != -1 )
				i++;	
		}
		return i;
	}
	
	public Iterator getIterator()
	{
		return new TypeMapIterator();
	}
	
	public TypeMap( TypeLibrary library )
	{
		_library = library;
		_mapForward = new ArrayList();
		_mapBack = new ArrayList();
		_readers = new ArrayList();
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
		if ( systemId == NOTYPE  )
			throw new TypeException( "typeid not valid" + systemId );

		if ( _mapForward.size() > id )
		{
			Integer intId = (Integer) _mapForward.get( id );
			if ( intId != null && intId.intValue() != NOTYPE )
			{
				if ( intId.intValue() != systemId )
					throw new TypeException( "id already mapped to different type" );
			}
		}
				
		_readers.ensureCapacity( id+1 );
		while (id >= _readers.size() ) _readers.add( null );
				
		_mapForward.ensureCapacity( id+1 );
		while( id >= _mapForward.size() ) _mapForward.add( new Integer(-1) );
		_mapForward.set( id, new Integer( systemId ) );
		
		_mapBack.ensureCapacity( systemId+1 );
		while ( systemId >= _mapBack.size() ) _mapBack.add( new Integer(-1) );
		_mapBack.set( systemId,  new Integer( id ) );		
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

		//return true;
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
		int i = _library.getId( name );
		
		if ( i >= _mapBack.size() )
			throw new TypeException( "not mapped: " + name );
		
		Integer intId = (Integer) _mapBack.get( i );
		if ( intId == null )
			throw new TypeException( "not mapped" );
		
		if ( intId.intValue() == NOTYPE )
			throw new TypeException( "not mapped " + name );
			
		return intId.intValue();
	}


	public String getName(int id) throws TypeException
	{
		return _library.getName( getSystemId( id ) );
	}

	public void setReader( int id, TypeReader reader ) throws TypeException
	{
	    _readers.add( id, reader );
	}
	
	public TypeReader getReader(int id) throws TypeException
	{
	    if (id >= _readers.size())
	    {
	        throw new TypeException("not mapped");
	    }
	    
	    TypeReader reader = (TypeReader) _readers.get( id );
	    if (reader != null )
	    {
	        return reader;
	    }
	    
		return _library.getReader( getSystemId(id) );
	}

	public TypeElement getStructure(int id) throws TypeException
	{
		return _library.getStructure( getSystemId(id) );
	}

	public TypeWriter getWriter(int id) throws TypeException
	{		
		return _library.getWriter( getSystemId( id ) );
	}

	public Class getClass( int id )
	throws TypeException
	{
		return _library.getClass( getSystemId(id ));
	}
	
	public int getId( Class clss) 
	throws TypeException
	{
		int id = _library.getId( clss ); 
		if ( id == -1 )
			throw new TypeException( "not found-" + clss.getName() );
		
		if ( id >= _mapBack.size() )
			throw new TypeException( "not mapped" );
			
		Integer i = (Integer) _mapBack.get( id );
		if ( i == null )
			throw new TypeException( "not mapped");
		
		if ( i.intValue() == NOTYPE )
			throw new TypeException( "mapped to invalid type. " + id + " " + clss.getName() );
			
		return i.intValue(); 		
	}

	public int getSystemId( int id )
	throws TypeException
	{
		if ( id == NOTYPE )
			throw new TypeException( "invalid type id" + id );
		
		if ( id >= _mapForward.size() )
			throw new TypeException( "not mapped:" + id );
			
		Integer i = (Integer) _mapForward.get( id );
		if ( i == null )
			throw new TypeException( "not found");

		if ( i.intValue() == -1 )
			throw new TypeException( "not mapped " + id );

		return i.intValue();		
	}

	public int getId( int systemid )
	throws TypeException
	{
		if ( systemid == NOTYPE )
			throw new TypeException( "invalid type id " + systemid );

		if ( systemid >= _mapBack.size() )
			throw new TypeException( "not mapped " + getLibrary().getName(systemid) );
		
		Integer i = (Integer) _mapBack.get( systemid );
		if ( i == null )
			throw new TypeException( "not found" );
			
		if ( i.intValue() == NOTYPE )
			throw new TypeException( "not mapped " + getLibrary().getName(systemid) );
		
		return i.intValue();
	}

	public TypeLibrary getLibrary()
	{
		return _library;
	}
	
	public boolean isValid( int id )
	{
		if ( id >= _mapForward.size() )
			return false;
			
		Integer i = (Integer) _mapForward.get( id );
		if ( i == null )
			return false;
			
		if ( i.intValue() == -1 )
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
		catch (TypeException e)
		{
			return false;
		}
		
		if ( i >= _mapBack.size() )
			return false;
		
		Integer intId = (Integer) _mapBack.get( i );
		if ( intId == null )
			return false;
			
		if ( intId.intValue() == NOTYPE )
			return false;
			
		return true;		
	}
}
