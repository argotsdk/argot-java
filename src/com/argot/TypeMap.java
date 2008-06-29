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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.argot.common.DateS64;
import com.argot.common.U16ArrayByte;
import com.argot.common.U32ArrayByte;
import com.argot.common.Int8;
import com.argot.common.Int32;
import com.argot.common.Int64;
import com.argot.common.Int16;
import com.argot.common.UInt8;
import com.argot.common.UInt32;
import com.argot.common.UInt64;
import com.argot.common.UInt16;
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
	
	public List getIdList()
	{
		List list = new ArrayList();
		Iterator i = _map.iterator();
		while ( i.hasNext() )
		{
			list.add( i.next() );
		}
		Collections.sort( list, new IntegerComparator() );
		return list;
	}
	
	private class IntegerComparator
	implements Comparator
	{
		public int compare(Object arg0, Object arg1) {
			Integer i1 = (Integer) arg0;
			Integer i2 = (Integer) arg1;	
			return i1.intValue() - i2.intValue();
		}	
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
		if ( !isValid( UInt8.TYPENAME ) )
			map( base+1, getLibrary().getId( UInt8.TYPENAME ) );
		
		if ( !isValid( UInt16.TYPENAME ) )
			map( base+2, getLibrary().getId( UInt16.TYPENAME ) );
			
		if ( !isValid( UInt32.TYPENAME ) )
			map( base+3, getLibrary().getId( UInt32.TYPENAME ));
					
		if ( !isValid( UInt64.TYPENAME ) )
			map( base+4, getLibrary().getId( UInt64.TYPENAME ));
			
		// Map Signed types.
		if ( !isValid( Int8.TYPENAME ) )
			map( base+5, getLibrary().getId( Int8.TYPENAME ) );

		if ( !isValid( Int16.TYPENAME ) )
			map( base+6, getLibrary().getId( Int16.TYPENAME ) );
			
		if ( !isValid( Int32.TYPENAME ) )
			map( base+7, getLibrary().getId( Int32.TYPENAME ));
					
		if ( !isValid( Int64.TYPENAME ) )
			map( base+8, getLibrary().getId( Int64.TYPENAME ));
	
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
	public void isSame( int id, String name, byte[] structure, ReferenceTypeMap coreMap )
	throws TypeException
	{
		// First check if we can find the same identifier.
		int i = _library.getId(name);
			
		// Are the identifiers the same.
		if ( id != i )
		{
			throw new TypeException("Type Mismatch: Type identifiers different");
		}

		// Are the definitions the same.
		// read the definition.
		coreMap.setReferenceMap(this);
		TypeElement definition = readStructure( coreMap, structure );
		
		// check what we've read with the local version.
		TypeElement localStruct = _library.getStructure( i );
		if (!TypeHelper.structureMatches( coreMap, definition, localStruct ))
		{
			throw new TypeException("Type mismatch: structures do not match: " + name);
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

	public void setReader( int id, TypeLibraryReader reader ) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		item.reader = reader;
	}
	
	public void setWriter( int id, TypeLibraryWriter writer ) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );		
		item.writer = writer;
	}	
	
	public TypeReader getReader(int id) throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		if ( item == null )
		{
			// chance that this isn't mapped yet.  Calling getSystemId will force dynamic mapping if required.
			getSystemId( id );
			
			// try again.
			item = (TypeMapItem) _map.getObjectFromKey( id );
		}

		if ( item.builtReader != null )
		{
			return item.builtReader;
		}
		
		if ( item.reader == null )
		{
			item.reader = _library.getReader( getSystemId( id ));
			item.readerBound = true;
		}
				
		if ( !item.readerBound )
		{
			if ( item.reader instanceof TypeBound )
			{
				TypeBound bind = (TypeBound) item.reader;
				int systemId = this.getSystemId(id);
				bind.bind( _library, _library.getStructure(systemId), _library.getName(systemId), systemId);
				item.readerBound = true;
			}			
		}
		
		item.builtReader = item.reader.getReader(this);
		return item.builtReader;
	}

	public TypeWriter getWriter(int id) throws TypeException
	{	
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( id );
		if ( item == null )
		{
			// chance that this isn't mapped yet.  Calling getSystemId will force dynamic mapping if required.
			getSystemId( id );
			
			// try again.
			item = (TypeMapItem) _map.getObjectFromKey( id );
		}
		
		if ( item.builtWriter != null )
		{
			return item.builtWriter;
		}
		
		if ( item.writer == null )
		{
			item.writer = _library.getWriter( getSystemId( id ));
			item.writerBound = true;
		}
		
		if ( !item.writerBound )
		{
			if ( item.writer instanceof TypeBound )
			{
				TypeBound bind = (TypeBound) item.writer;
				int systemId = this.getSystemId(id);
				bind.bind( _library, _library.getStructure(systemId), _library.getName(systemId), systemId);
				item.writerBound = true;
			}						
		}
		
		item.builtWriter = item.writer.getWriter(this);
		return item.builtWriter;
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
	
	public boolean isSimpleType( int id )
	throws TypeException
	{
		return _library.isSimpleType( getSystemId(id) );		
	}
	
	public class TypeMapItem
	{
		public TypeReader builtReader;
		public TypeLibraryReader reader;
		public boolean readerBound;
		public TypeWriter builtWriter;
		public TypeLibraryWriter writer;
		public boolean writerBound;
	}
}
