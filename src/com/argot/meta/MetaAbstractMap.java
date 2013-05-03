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
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeLocationRelation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UVInt28;


/**
 * MetaAbstractMap defines mappings between an abstract type and a real type.
 * It maps the name of the type, not the definition.
 * 
 * 
 * @author davidryan
 */
public class MetaAbstractMap
extends MetaExpression
implements MetaDefinition
{
    public static final String TYPENAME = "meta.abstract_map";
    public static final String VERSION = "1.3";
    
	private TypeLibrary _library;
	
	private boolean _isBound;
	
	private int _abstractType;
	private int _concreteType;
	
	private MetaAbstract _metaAbstract;
	
	public MetaAbstractMap( int concreteType ) throws TypeException
	{
		_isBound = false;
		
		if (concreteType < 0 )
		{
			throw new TypeException("Invalid Type identifier for concrete type");
		}
		
		_concreteType = concreteType;
	}

    public String getTypeName()
    {
        return TYPENAME;
    }
    
	public int getExpressionType( TypeLibrary library ) throws TypeException
	{
	    return library.getDefinitionId( TYPENAME, VERSION );
	}

	public void check( TypeLibrary library ) throws TypeException
	{
		_library = library;
		
		if ( _library.getTypeState(  _concreteType ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			throw new TypeException( "concrete type not valid");
		}
		
		MetaExpression abstractElement = (MetaExpression) _library.getStructure( _abstractType );		
		if ( !(abstractElement instanceof MetaAbstract ))
		{
			throw new TypeException( "abstract map to non-abstract type");			
		}
		
		_metaAbstract = (MetaAbstract) abstractElement;
	}

    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) 
    throws TypeException
    {
        super.bind(library, definitionId, location, definition);
        
        _isBound = true;
        
        if (!(location instanceof TypeLocationRelation))
        {
        	throw new TypeException("Expected TypeLocationRelation");
        }
        
        TypeLocationRelation relation = (TypeLocationRelation) location;
        _abstractType = relation.getId();
        
        check( library );
        
        TypeElement concreteElement = _library.getStructure( _concreteType ); 
        if (concreteElement instanceof MetaAbstract)
        {
        	((MetaAbstract)concreteElement).addAbstractMap(_metaAbstract);
        }
        else if (!(concreteElement instanceof MetaIdentity))
        {
        	throw new TypeException("Can only map abstract types to identity or abstract definitions");
        }
        _metaAbstract.addMap( _concreteType, definitionId, false );
    }
	
    public int getAbstractType()
    {
    	return _abstractType;
    }
    
	public int getConcreteType()
	{
		return _concreteType;
	}
	

	public void setConcreteType(int i) 
	throws TypeException
	{
		if (_isBound) throw new TypeException("map already bound");
		
		_concreteType = i;        
	}
	
	public static class MetaMapTypeReader
	implements TypeReader,TypeBound
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaAbstractMap.class );
		
		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_reader.bind(library, definitionId, definition);
		}
		
	    public Object read(TypeInputStream in) throws TypeException, IOException
	    {
	    	TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaAbstractMap map = (MetaAbstractMap) reader.read( in );
			// before we return this we need to change the
			// values from the mapped to the internal values.
			
			TypeMap refMap = (TypeMap) in.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
			
			if (  refMap.isValid( map.getConcreteType() ) )
			{
				map.setConcreteType( refMap.getNameId( map.getConcreteType () ));
			}
			else
			{
				throw new TypeException( "TypeReference: invalid id " );
			}
			return map;				
	    }
	}
	
	public static class MetaMapTypeWriter
	implements TypeWriter
	{
	    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
	    {
			MetaAbstractMap tr = (MetaAbstractMap) o;
			TypeMap refMap = (TypeMap) out.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
			int concreteId = refMap.getStreamId( tr._concreteType );
			out.writeObject( UVInt28.TYPENAME, new Integer( concreteId ) );
	    }   
	}

	private class MetaMapReader
	implements TypeReader
	{
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
	        // This probably shouldn't happen.
	        // Someone reads an abstract type.  Which checks its local
	        // list and reads directly.  
	        
	        //  But just in case. Just read the concrete type.
	        return in.readObject( in.getTypeMap().getStreamId(_concreteType) );
		}
		
	}
	
    public TypeReader getReader(TypeMap map) 
    throws TypeException
    {
    	return new MetaMapReader();
    }
	
	
	private class MetaMapWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
	    	out.writeObject(UVInt28.TYPENAME, new Integer(out.getTypeMap().getStreamId(getMemberTypeId()) ));
			
			// This will force the mapId to be mapped in dynamic type maps.
			out.getTypeMap().getStreamId(getMemberTypeId() );
			out.writeObject( out.getTypeMap().getStreamId( _concreteType ), o );    	
		}
	}
	
    public TypeWriter getWriter(TypeMap map) 
    throws TypeException
    {
    	return new MetaMapWriter();
    }
}
