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
package com.argot.meta;

import java.io.IOException;

import com.argot.ReferenceTypeMap;
import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UInt16;

public class MetaChoiceMap
extends MetaExpression
implements MetaDefinition
{
    public static final String TYPENAME = "meta.choice.map";
    public static final String TYPENAME_VERSION = "1.3";
    
	private TypeLibrary _library;
	
	private int _abstractType;
	private int _concreteType;
	
	private MetaAbstract _metaAbstract;
	
	public MetaChoiceMap( int abstractType, int concreteType ) throws TypeException
	{
		_abstractType = abstractType;
		_concreteType = concreteType;
	}

    public String getTypeName()
    {
        return TYPENAME;
    }
    
	public int getExpressionType( TypeLibrary library ) throws TypeException
	{
	    return library.getDefinitionId( TYPENAME, TYPENAME_VERSION );
	}
	
	public String getMapTypeName( TypeLibrary library ) throws TypeException
	{
		MetaName abstractName = library.getName( _abstractType );
		MetaName concreteName = library.getName( _concreteType );
		
		return abstractName + "#" + concreteName;
	}
	
	public void check( TypeLibrary library ) throws TypeException
	{
		_library = library;
		
		if ( _library.getTypeState( _abstractType ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			throw new TypeException( "abstract type not valid");
		}
		
		if ( _library.getTypeState(  _concreteType ) == TypeLibrary.TYPE_NOT_DEFINED )
		{
			throw new TypeException( "concrete type not valid");
		}
		
		MetaExpression abstractElement = (MetaExpression) _library.getStructure( _abstractType );
		
		if ( !(abstractElement instanceof  MetaAbstract ))
		{
			throw new TypeException( "abstract map to non-abstract type");
		}
		
		_metaAbstract = (MetaAbstract) abstractElement;
	}

    public void bind(TypeLibrary library, int definitionId, TypeLocation location, TypeElement definition) 
    throws TypeException
    {
        super.bind(library, definitionId, location, definition);
        check( library );
        
        TypeElement concreteElement = _library.getStructure( _concreteType ); 
        if (concreteElement instanceof MetaAbstract)
        {
        	((MetaAbstract)concreteElement).addAbstractMap(_metaAbstract);
        }
    }	
    
	public int getAbstractType()
	{
		return _abstractType;
	}
	
	public int getConcreteType()
	{
		return _concreteType;
	}

	private void setConcreteType(int i)
	{
		_concreteType = i;        
	}

	private void setAbstractType(int i)
	{
		_abstractType = i;
	}
	
	public static class MetaMapTypeReader
	implements TypeReader,TypeBound
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaChoiceMap.class );
		
		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_reader.bind(library, definitionId, definition);
		}
		
	    public Object read(TypeInputStream in) throws TypeException, IOException
	    {
	    	TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaChoiceMap map = (MetaChoiceMap) reader.read( in );
			// before we return this we need to change the
			// values from the mapped to the internal values.
			
			ReferenceTypeMap mapCore = (ReferenceTypeMap) in.getTypeMap();
					
			if (  mapCore.referenceMap().isValid( map.getAbstractType() ) )
			{
				map.setAbstractType( mapCore.referenceMap().getDefinitionId( map.getAbstractType () ));
			}
			else
			{
				throw new TypeException( "TypeReference: invalid id " );
			}
			
			if (  mapCore.referenceMap().isValid( map.getConcreteType() ) )
			{
				map.setConcreteType( mapCore.referenceMap().getDefinitionId( map.getConcreteType () ));
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
			MetaChoiceMap tr = (MetaChoiceMap) o;
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			int abstractId = mapCore.referenceMap().getStreamId( tr._abstractType );
			out.writeObject( UInt16.TYPENAME, new Integer( abstractId ));
			int concreteId = mapCore.referenceMap().getStreamId( tr._concreteType );
			out.writeObject( UInt16.TYPENAME, new Integer( concreteId ) );
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
	    	out.writeObject(UInt16.TYPENAME, new Integer(out.getTypeMap().getStreamId(getMemberTypeId()) ));
			
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
