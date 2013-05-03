/*
 * Copyright (c) 2003-2010, Live Media Pty. Ltd.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * provided that the following conditions are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice, this list of
 *     conditions and the following disclaimer.
 *  2. Redistributions in binary form must reproduce the above copyright notice, this list of
 *     conditions and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *  3. Neither the name of Live Media nor the names of its contributors may be used to endorse
 *     or promote products derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY
 * AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.argot.model;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;

public class AbstractData 
extends ModelData
{
	private MetaAbstract _metaAbstract;
	private int _type;
	private Object _data;
	
	public AbstractData( MetaAbstract metaAbstract, int type, Object data )
	{
		_metaAbstract = metaAbstract;
		_type = type;
		_data = data;
	}
	
	public Object getData() 
	{
		return _data;
	}

	public MetaExpression getStructure() 
	{
		return _metaAbstract;
	}
	
	public static class AbstractDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaAbstract metaAbstract = (MetaAbstract) element;
			return new AbstractDataReader(metaAbstract, map.getReader(map.getStreamId(UInt16.TYPENAME)));		
		}	
	}
	
	private static class AbstractDataReader
	implements TypeReader
	{
		private MetaAbstract _metaAbstract;
		private TypeReader _uint16;
		private Map<Integer,TypeReader> _mapCache;
		
		public AbstractDataReader(MetaAbstract metaAbstract, TypeReader uint16)
		{
			_metaAbstract = metaAbstract;
			_uint16 = uint16;
			_mapCache = new HashMap<Integer,TypeReader>();
		}
		
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			Object u16obj = _uint16.read(in);
			Integer type = null;
			if (u16obj instanceof SimpleData)
			{
				type = (Integer) ((SimpleData)u16obj).getData();
			}
			else
			{
				type = (Integer) u16obj;
			}
	        //Integer type = (Integer) _uint16.read(in);
	        TypeReader mappedReader = (TypeReader) _mapCache.get(type);
	        if ( mappedReader == null )
	        {
	        	int id = mapType( in.getTypeMap(), type );
	        	mappedReader = in.getTypeMap().getReader(id);
	        	_mapCache.put(type, mappedReader);
	        }
	        int systemType = in.getTypeMap().getDefinitionId(type.intValue());
	        return new AbstractData( _metaAbstract,systemType,mappedReader.read( in ));
		}
		
		private int mapType(TypeMap map, Integer type)
		throws TypeException
		{
	        int mapId = map.getDefinitionId( type.intValue() );
			int concrete = _metaAbstract.getConcreteId( mapId );
	        if ( concrete == -1 )
	        {
	        	throw new TypeException("type not mapped:" + type.intValue() + " " + map.getName( type.intValue() ) ); 
	        }
			return map.getStreamId( mapId );
		}
		
	}


	public static class AbstractDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaAbstract metaAbstract = (MetaAbstract) element;
			return new AbstractDataWriter(metaAbstract, map.getWriter(map.getStreamId(UInt16.TYPENAME)));		
		}
	}
	
	private static class CacheEntry
	{
		Integer mapId;
		TypeWriter idWriter;
	}	
	
    private static class AbstractDataWriter
    implements TypeWriter
    {
    	private MetaAbstract _metaAbstract;
    	private TypeWriter _uint16;
		private Map<Integer,CacheEntry> _mapCache;
		
    	public AbstractDataWriter(MetaAbstract metaAbstract, TypeWriter uint16)
    	{
    		_metaAbstract = metaAbstract;
    		_uint16 = uint16;
    		_mapCache = new HashMap<Integer,CacheEntry>();
    	}
    	
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if (!(o instanceof AbstractData))
			{
				throw new TypeException("bad data");
			}
			AbstractData data = (AbstractData) o;

			//int type = data.getStructure().getMemberTypeId();
			int type = data._type;
			CacheEntry entry = (CacheEntry) _mapCache.get(new Integer(type));
			if (entry == null )
			{
				entry = mapType(out.getTypeMap(), type);
				_mapCache.put(new Integer(type), entry);
			}
			_uint16.write(out, entry.mapId);
			entry.idWriter.write(out, data.getData());
		}
    	
		private CacheEntry mapType(TypeMap map, int type)
		throws TypeException
		{
			int mapId = _metaAbstract.getConcreteId( type );
	        if ( mapId == -1 )
	        {
				throw new TypeException( "can't write abstract type directly.:" );
			}
	        
	        CacheEntry entry = new CacheEntry();
	        
	        // This is very important.  When working as a remote client we must ensure
	        // that the server also has the mapping from concrete to map value.  By 
	        // getting the System Id of the MapId it will automatically resolve if the
	        // server has the correct value.
	        map.getStreamId(mapId);
	        
	        // The Id written to file is the mapped concrete id.
	        entry.mapId = new Integer(map.getStreamId(type));
	        
	        // Cache the writer function.
	        //System.out.println("Abstract getting writer for: " + map.getName(map.getId(type)) );
	        entry.idWriter = map.getWriter(map.getStreamId( type ));
			return entry;
		}
		
    }

}
