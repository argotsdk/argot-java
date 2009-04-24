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
package com.argot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.argot.meta.MetaName;
import com.argot.util.TwoWayHashMap;

/**
 * A TypeMap is used to map an external systems type identifiers to
 * the internal TypeLibrary of the host system.
 * 
 * When using named types, there must be a single definition mapped
 * to a name.  A name,version combination may be used to specify
 * a specific version.
 *
 * A type name must have a mapping for its identity and a mapping
 * for a specific definition of the name.  Requires:
 * 
 * name -> stream id (specific definition)
 * name -> identity stream id
 * 
 */
public class TypeMap
{
    public static int NOTYPE = -1;
    
    // base library.
	TypeLibrary _library;
	
	// interface to handle mapping.
	TypeMapper _mapper;
	
	// mapping between stream id and definition id.
	TwoWayHashMap _map;
	
	// mapping between names and stream id.
	Map _nameMap;

	// mapping between name id and stream id
	TwoWayHashMap _nameIdMap;

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
	
	public TypeMap( TypeLibrary library, TypeMapper mapper ) 
	throws TypeException
	{
		if (library == null)
			throw new IllegalArgumentException();
		
		if (mapper == null)
			throw new IllegalArgumentException();
		
		_library = library;
		_mapper = mapper;
		_map = new TwoWayHashMap();
		_nameMap = new HashMap();
		_nameIdMap = new TwoWayHashMap();
		
		_mapper.initialise(this);
	}


	public TypeLibrary getLibrary()
	{
		return _library;
	}	
	
	public void map( int streamId, int definitionId )
	throws TypeException
	{
		int i;
		
		if ( definitionId == NOTYPE  )
			throw new TypeException( "typeid not valid" + definitionId );

		// check if it hasn't already been mapped.
		if ( (i = _map.findValue( streamId )) != -1 )
		{
			if ( i != definitionId )
				throw new TypeException("id already mapped to different type. streamId:" + streamId + " mappedTo:"+ i + " trying to map to:" + definitionId );

			// already mapped to the same value.
			return;
		}
		
		//System.out.println("mapping: " + streamId + " to: " + definitionId + "(" + _library.getName(definitionId) + ")");

		TypeMapItem item = new TypeMapItem();
		

		TypeLocation location = _library.getLocation( definitionId );
		if (location instanceof TypeLocationDefinition)
		{
			
			TypeLocationDefinition definition = (TypeLocationDefinition) location;
			int identityId = definition.getId();
			_nameIdMap.add( streamId, identityId, item);
		}
		else if (location instanceof TypeLocationName)
		{
			throw new TypeException("Names may not be mapped directly.");
		}
			
		_map.add( streamId, definitionId, item );
	}

	// Functions to override the behaviour of TypeLibrary.

	public void setReader( int streamId, TypeLibraryReader reader ) 
	throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( streamId );
		item.reader = reader;
	}
	
	public void setWriter( int streamId, TypeLibraryWriter writer ) 
	throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( streamId );		
		item.writer = writer;
	}	

	
	
	// Functions that return the stream identifier.

	/**
	 * returns the stream identifier for a given name.  
	 * If type needs to be mapped then the version required must be resolved by the TypeMapper.
	 * 
	 * Lookup nameMap.
	 * 
	 */
	public int getStreamId(String name)
	throws TypeException
	{
		if (name == null)
			throw new TypeException("Invalid name");
		
		int identityId = _library.getTypeId(name);
		int streamId = _nameIdMap.findKey(identityId);
		if (streamId == -1)
		{
			return _mapper.mapDefault(identityId);
		}
		return streamId;
	}
	
	/**
	 * Given a TypeLibrary definition identifier, it returns the stream identifier.
	 * This is the same as specifying a name and version.
	 * 
	 * @param definitionId
	 * @return
	 * @throws TypeException
	 */
	public int getStreamId( int definitionId )
	throws TypeException
	{
		int streamId = _map.findKey(definitionId);
		if (streamId == -1)
		{
			TypeLocation location = this._library.getLocation(definitionId);
			if (location instanceof TypeLocationName)
			{
				streamId = _nameIdMap.findKey(definitionId);
				if (streamId == -1)
				{
					return _mapper.mapDefault(definitionId);
				}
				return streamId;				
			}
			return _mapper.map(definitionId);
		}
		return streamId;
	}
	
	/**
	 * returns the stream identifier given a class.  A class must be unique
	 * in each TypeLibrary.
	 * 
	 * @param clss
	 * @return
	 * @throws TypeException
	 */
	public int getStreamId( Class clss) 
	throws TypeException
	{
		int definitionId = _library.getId(clss);

		int streamId = _map.findKey(definitionId);
		if (streamId == -1)
		{
			return getStreamId(definitionId);
		}
		return streamId;	
	}
	
	// Functions that use stream identifier to return type data.

	/**
	 *  Given a stream identifier, return a definition identifier.
	 *  This function must be called by all other functions that take
	 *  a stream identifier as input.  If the type is not mapped, it
	 *  attempts a reverse map via the TypeMapper.
	 */
	public int getDefinitionId( int streamId )
	throws TypeException
	{
		int definitionId = _map.findValue(streamId);
		if (definitionId == -1)
		{
			definitionId = _mapper.mapReverse(streamId);
		}
		return definitionId;	
	}
	
	public int getNameId( int streamId )
	throws TypeException
	{
		int defId = getDefinitionId( streamId );
		TypeLocation location = this._library.getLocation(defId);
		if ( location instanceof TypeLocationDefinition )
		{
			return ((TypeLocationDefinition)location).getId();
		}
		else if (location instanceof TypeLocationRelation )
		{
			return ((TypeLocationRelation)location).getId();
		}
		throw new TypeException("Unknown location type");
	}
	
	/**
	 * Given a streamId, return the TypeReader.
	 * If the streamId has not been mapped, attempt a reverse map via the TypeMapper.
	 * If a TypeReader has been set, this will be returned, otherwise the default
	 * TypeReader as set by the TypeLibrary will be used.
	 * 
	 * @param streamId
	 * @return
	 * @throws TypeException
	 */
	public TypeReader getReader(int streamId) 
	throws TypeException
	{
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( streamId );
		if ( item == null )
		{
			// chance that this isn't mapped yet.  Call mapReverse to for mapping.
			_mapper.mapReverse(streamId);
			
			// try again.
			item = (TypeMapItem) _map.getObjectFromKey( streamId );
			if ( item == null )
			{
				throw new TypeException("Type not mapped");
			}
		}
		
		if ( item.builtReader != null )
		{
			return item.builtReader;
		}
		
		if ( item.reader == null )
		{
			item.reader = _library.getReader( getDefinitionId( streamId ));
			item.readerBound = true;
		}
				
		if ( !item.readerBound )
		{
			if ( item.reader instanceof TypeBound )
			{
				TypeBound bind = (TypeBound) item.reader;
				int systemId = this.getDefinitionId(streamId);
				bind.bind( _library, systemId, _library.getStructure(systemId));
				item.readerBound = true;
			}			
		}
		
		item.builtReader = item.reader.getReader(this);
		return item.builtReader;
	}

	public TypeWriter getWriter(int streamId) 
	throws TypeException
	{	
		TypeMapItem item = (TypeMapItem) _map.getObjectFromKey( streamId );
		if ( item == null )
		{
			// chance that this isn't mapped yet.  Call mapReverse to for mapping.
			_mapper.mapReverse(streamId);
			
			// try again.
			item = (TypeMapItem) _map.getObjectFromKey( streamId );
			if ( item == null )
			{
				throw new TypeException("Type not mapped");
			}
		}
		
		if ( item.builtWriter != null )
		{
			return item.builtWriter;
		}
		
		if ( item.writer == null )
		{
			item.writer = _library.getWriter( getDefinitionId( streamId ));
			item.writerBound = true;
		}
		
		if ( !item.writerBound )
		{
			if ( item.writer instanceof TypeBound )
			{
				TypeBound bind = (TypeBound) item.writer;
				int systemId = this.getDefinitionId(streamId);
				bind.bind( _library, systemId, _library.getStructure(systemId));
				item.writerBound = true;
			}						
		}
		
		item.builtWriter = item.writer.getWriter(this);
		return item.builtWriter;
	}

	/**
	 *  Given a stream identifier, return the name of the type.
	 *  If the streamId is not valid the TypeMapper should attempt a 
	 *  reverse map of the type.
	 *  
	 */
	public MetaName getName(int streamId) 
	throws TypeException
	{
		return _library.getName( getDefinitionId( streamId ) );
	}
	
	public String getVersion(int streamId)
	throws TypeException
	{
		return _library.getVersion( getDefinitionId( streamId ));
	}
	
	public TypeLocation getLocation(int streamId) 
	throws TypeException
	{
		return _library.getLocation( getDefinitionId(streamId));
	}

	
	public TypeElement getStructure(int streamId) 
	throws TypeException
	{
		return _library.getStructure( getDefinitionId(streamId) );
	}

	public Class getClass( int streamId )
	throws TypeException
	{
		return _library.getClass( getDefinitionId(streamId ));
	}
	
	public boolean isSimpleType( int streamId )
	throws TypeException
	{
		return _library.isSimpleType( getDefinitionId(streamId) );		
	}
	
	public boolean isValid( int streamId )
	{
		return (-1 == _map.findValue(streamId) ? false:true);
	}
	
	public boolean isMapped( int definitionId )
	{
		return (-1 == _map.findKey(definitionId) ? false:true );
	}
	
	// Helper function.
	
	public boolean isValid( String name )
	{
		try 
		{
			return isValid(getStreamId(name));
		} 
		catch (TypeException e) 
		{
			return false;
		}
	}
	
	
	private class TypeMapItem
	{
		public TypeReader builtReader;
		public TypeLibraryReader reader;
		public boolean readerBound;
		public TypeWriter builtWriter;
		public TypeLibraryWriter writer;		
		public boolean writerBound;
		public int version;
	}
	
}