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

import com.argot.ReferenceTypeMap;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeLibrary;
import com.argot.TypeWriter;

public class MetaMap
extends MetaBase
implements MetaExpression, MetaDefinition
{
    public static final String TYPENAME = "meta.map";
    
	private TypeLibrary _library;
	
	private int _abstractType;
	private int _concreteType;
	
	private MetaAbstract _metaAbstract;
	
	public MetaMap( int abstractType, int concreteType ) throws TypeException
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
	    return library.getId( TYPENAME );
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

    public void bind(TypeLibrary library, TypeElement definition,
            String typeName, int typeId) throws TypeException
    {
        super.bind(library, definition, typeName, typeId);
        check( library );
		_metaAbstract.addMap( _concreteType, typeId );
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
	implements TypeReader
	{
	    public Object read(TypeInputStream in, TypeElement element) throws TypeException, IOException
	    {
	        // was instanceof MetaDefinition.  maybe wrong.
			if ( element instanceof MetaExpression )
			{
				TypeReader reader = new TypeReaderAuto( MetaMap.class );
				MetaMap map = (MetaMap) reader.read( in, element );
				// before we return this we need to change the
				// values from the mapped to the internal values.
				
				ReferenceTypeMap mapCore = (ReferenceTypeMap) in.getTypeMap();
						
				if (  mapCore.referenceMap().isValid( map.getAbstractType() ) )
				{
					map.setAbstractType( mapCore.referenceMap().getSystemId( map.getAbstractType () ));
				}
				else
				{
					throw new TypeException( "TypeReference: invalid id " );
				}
				
				if (  mapCore.referenceMap().isValid( map.getConcreteType() ) )
				{
					map.setConcreteType( mapCore.referenceMap().getSystemId( map.getConcreteType () ));
				}
				else
				{
					throw new TypeException( "TypeReference: invalid id " );
				}
				return map;
				
				
			}
			throw new TypeException( "shouldn't get here.");
	    }
	}
	
	public static class MetaMapTypeWriter
	implements TypeWriter
	{
	    public void write(TypeOutputStream out, Object o, TypeElement element) throws TypeException, IOException
	    {
			MetaMap tr = (MetaMap) o;
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			int abstractId = mapCore.referenceMap().getId( tr._abstractType );
			out.writeObject( "u16", new Integer( abstractId ));
			int concreteId = mapCore.referenceMap().getId( tr._concreteType );
			out.writeObject( "u16", new Integer( concreteId ) );
	    }
	}
	
    public void doWrite(TypeOutputStream out, Object o) throws TypeException, IOException
    {
    	out.writeObject("u16", new Integer(out.getTypeMap().getId(this.getMemberTypeId()) ));
		
		// This will force the mapId to be mapped in dynamic type maps.
		out.getTypeMap().getId( this.getMemberTypeId() );
		out.writeObject( out.getTypeMap().getId( _concreteType ), o );    	
    }

    public Object doRead(TypeInputStream in) throws TypeException, IOException
    {
        // This probably shouldn't happen.
        // Someone reads an abstract type.  Which checks its local
        // list and reads directly.  
        
        //  But just in case. Just read the concrete type.
        return in.readObject( in.getTypeMap().getId(_concreteType) );
    }

}
