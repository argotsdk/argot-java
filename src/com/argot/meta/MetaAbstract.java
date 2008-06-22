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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;

public class MetaAbstract
extends MetaExpression
implements MetaDefinition
{
    public static final String TYPENAME = "meta.abstract";
    
	private Map _concreteToMap;
	private Map _mapToConcrete;
	
	// This keeps a list of any abstract types we've mapped.
	private List _abstractParentMap;
	
	public MetaAbstract()
	{
		_concreteToMap = new HashMap();
		_mapToConcrete = new HashMap();
		_abstractParentMap = new ArrayList();
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public Map getConcreteToMap()
    {
    	return _concreteToMap;
    }
    
    public Map getMapToConcrete()
    {
    	return _mapToConcrete;
    }
	
    public void addAbstractMap( MetaAbstract parent )
    {
    	_abstractParentMap.add( parent );
    	
    	Iterator i = _concreteToMap.keySet().iterator();
    	while( i.hasNext())
    	{
    		Integer c = (Integer) i.next();
    		Integer m = (Integer) _concreteToMap.get(c);
    		
    		parent.addMap( c.intValue(), m.intValue());
    	}
    	
    }
    
	public void addMap( int concreteType, int mapType )
	{
		_concreteToMap.put( new Integer( concreteType ), new Integer(mapType));
		_mapToConcrete.put( new Integer( mapType ), new Integer(concreteType));
		
    	Iterator i = _abstractParentMap.iterator();
    	while( i.hasNext())
    	{
    		MetaAbstract p = (MetaAbstract) i.next();
    		
    		p.addMap( concreteType, mapType);
    	}		
	}
	
	public static class MetaAbstractTypeReader
	implements TypeReader, TypeLibraryReader, MetaExpressionReader
	{
	    public Object read(TypeInputStream in) 
	    throws TypeException, IOException
	    {
			return new MetaAbstract();
	    }

		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaAbstract metaAbstract = (MetaAbstract) element;
			return new MetaAbstractReader(metaAbstract,map.getReader(map.getId(UInt16.TYPENAME)));
		}
	}

	public static class MetaAbstractTypeWriter
	implements TypeWriter, TypeLibraryWriter, MetaExpressionWriter
	{
	    public void write(TypeOutputStream out, Object o) 
	    throws TypeException, IOException
	    {
	    	// do nothing.
	    }

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaAbstract metaAbstract = (MetaAbstract) element;
			return new MetaAbstractWriter(metaAbstract,map.getWriter(map.getId(UInt16.TYPENAME)));
		}
	}

	private static class MetaAbstractReader
	implements TypeReader
	{
		private MetaAbstract _metaAbstract;
		private TypeReader _uint16;
		private Map _mapCache;
		
		public MetaAbstractReader(MetaAbstract metaAbstract, TypeReader uint16)
		{
			_metaAbstract = metaAbstract;
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
			Integer concrete = (Integer) _metaAbstract._concreteToMap.get( new Integer( mapId ));
	        if ( concrete == null )
	        {
	        	throw new TypeException("type not mapped:" + type.intValue() + " " + map.getName( type.intValue() ) ); 
	        }
			return map.getId( mapId );
		}
		
	}

	private static class CacheEntry
	{
		Integer mapId;
		TypeWriter idWriter;
	}
	
    private static class MetaAbstractWriter
    implements TypeWriter
    {
    	private MetaAbstract _metaAbstract;
    	private TypeWriter _uint16;
		private Map _mapCache;
		
    	public MetaAbstractWriter(MetaAbstract metaAbstract, TypeWriter uint16)
    	{
    		_metaAbstract = metaAbstract;
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
			Integer mapId = (Integer) _metaAbstract._concreteToMap.get( new Integer( id ));
	        if ( mapId == null )
	        {
				throw new TypeException( "can't write abstract type directly.:" + clss.getName() );
			}
	        
	        CacheEntry entry = new CacheEntry();
	        
	        // This is very important.  When working as a remote client we must ensure
	        // that the server also has the mapping from concrete to map value.  By 
	        // getting the System Id of the MapId it will automatically resolve if the
	        // server has the correct value.
	        map.getId(mapId.intValue());
	        
	        // The Id written to file is the mapped concrete id.
	        entry.mapId = new Integer(map.getId(id));
	        
	        // Cache the writer function.
	        entry.idWriter = map.getWriter(map.getId( id ));
			return entry;
		}
		
    }
 
	public boolean isMapped( int id )
	{
		return _concreteToMap.get( new Integer(id))==null?false:true;
	}

}
