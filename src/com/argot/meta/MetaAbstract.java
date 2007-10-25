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
package com.argot.meta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaAbstract
extends MetaBase
implements MetaExpression, MetaDefinition
{
    public static final String TYPENAME = "meta.abstract";
    
	private Map _concreteToMap;
	private Map _mapToConcrete;
	
	public MetaAbstract()
	{
		_concreteToMap = new HashMap();
		_mapToConcrete = new HashMap();
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
	
	public void addMap( int concreteType, int mapType )
	{
		_concreteToMap.put( new Integer( concreteType ), new Integer(mapType));
		_mapToConcrete.put( new Integer( mapType ), new Integer(concreteType));
	}
	
	public static class MetaAbstractTypeReader
	implements TypeReader
	{
	    public Object read(TypeInputStream in) throws TypeException, IOException
	    {
			return new MetaAbstract();
	    }
	}

	public static class MetaAbstractTypeWriter
	implements TypeWriter
	{
	    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
	    {
	    	// do nothing.
	    }
	}

	private class MetaAbstractReader
	implements TypeReader
	{
		private TypeReader _uint16;
		private Map _mapCache;
		
		public MetaAbstractReader(TypeReader uint16)
		{
			_uint16 = uint16;
			_mapCache = new HashMap();
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
	        Integer type = (Integer) _uint16.read(in);
	        TypeReader mappedReader = (TypeReader) _mapCache.get(type);
	        if ( mappedReader == null )
	        {
	        	int id = mapType( in.getTypeMap(), type );
	        	mappedReader = in.getTypeMap().getReader(id);
	        	_mapCache.put(type, mappedReader);
	        }	        
			return mappedReader.read( in );
		}
		
		private int mapType(TypeMap map, Integer type)
		throws TypeException
		{
	        int mapId = map.getSystemId( type.intValue() );
			Integer concrete = (Integer) _mapToConcrete.get( new Integer( mapId ));
	        if ( concrete == null )
	        {
	        	throw new TypeException("type not mapped:" + type.intValue() + " " + map.getName( type.intValue() ) ); 
	        }
			return map.getId( concrete.intValue());
		}
		
	}

    public TypeReader getReader(TypeMap map) 
    throws TypeException
    {
    	return new MetaAbstractReader(map.getReader(map.getId("u16")));
    }

	private static class CacheEntry
	{
		Integer mapId;
		TypeWriter idWriter;
	}
	
    private class MetaAbstractWriter
    implements TypeWriter
    {
    	private TypeWriter _uint16;
		private Map _mapCache;
		
    	public MetaAbstractWriter(TypeWriter uint16)
    	{
    		_uint16 = uint16;
    		_mapCache = new HashMap();
    	}
    	
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			Class clss = o.getClass();
			CacheEntry entry = (CacheEntry) _mapCache.get(clss);
			if (entry == null )
			{
				entry = mapType(out.getTypeMap(), clss);
				_mapCache.put(clss, entry);
			}
			_uint16.write(out, entry.mapId);
			entry.idWriter.write(out, o);
		}
    	
		private CacheEntry mapType(TypeMap map, Class clss)
		throws TypeException
		{
			int id = map.getLibrary().getId(clss);
			Integer mapId = (Integer) _concreteToMap.get( new Integer( id ));
	        if ( mapId == null )
	        {
				throw new TypeException( "can't write abstract type directly.:" + clss.getName() );
			}
	        
	        CacheEntry entry = new CacheEntry();
	        entry.mapId = new Integer(map.getId(mapId.intValue()));
	        entry.idWriter = map.getWriter(map.getId( id ));
			return entry;
		}
		
    }
    
    public TypeWriter getWriter(TypeMap map) 
    throws TypeException
    {
    	return new MetaAbstractWriter(map.getWriter(map.getId("u16")));
    }
    
	public boolean isMapped( int id )
	{
		return _concreteToMap.get( new Integer(id))==null?false:true;
	}

}
