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

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
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
	    public Object read(TypeInputStream in, TypeElement element) throws TypeException, IOException
	    {
			return new MetaAbstract();
	    }
	}

	public static class MetaAbstractTypeWriter
	implements TypeWriter
	{
	    public void write(TypeOutputStream out, Object o, TypeElement element) throws TypeException, IOException
	    {
	    }
	}

    public void doWrite(TypeOutputStream out, Object o) throws TypeException, IOException
    {
		int id = out.getTypeMap().getLibrary().getId(o.getClass());
		Integer mapId = (Integer) _concreteToMap.get( new Integer(id ));

		if ( mapId == null )
		{
			throw new TypeException( "can't write abstract type directly.:" + o.getClass().getName() );
		}
		out.writeObject("u16", new Integer(out.getTypeMap().getId(mapId.intValue()) ));
		
		// This will force the mapId to be mapped in dynamic type maps.
		out.getTypeMap().getId( mapId.intValue());
		out.writeObject( out.getTypeMap().getId( id ), o );
    }

    public Object doRead(TypeInputStream in) throws TypeException, IOException
    {
        Integer type = (Integer) in.readObject( "u16");
        int mapId = in.getTypeMap().getSystemId( type.intValue() );
		Integer concrete = (Integer) _mapToConcrete.get( new Integer( mapId ));
        if ( concrete == null )
        {
        	throw new TypeException("type not mapped:" + type.intValue() + " " + in.getTypeMap().getName( type.intValue() ) ); 
        }
		return in.readObject( in.getTypeMap().getId( concrete.intValue()) );
    }
	
	public boolean isMapped( int id )
	{
		return _concreteToMap.get( new Integer(id))==null?false:true;
	}

}
