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
import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;

public class MetaReference
extends MetaBase
implements MetaExpression
{
    public static String TYPENAME  = "meta.reference";
	
	private int _type;
	private String _description;

	public MetaReference( int type, String description )
	{
		_type = type;
		_description = description;
	}

    public String getTypeName()
    {
        return TYPENAME;
    }
    
	public int getType()
	{
		return _type;
	}
	
	private void setType( int type )
	{
		_type = type;
	}
	
	public String getMethod()
	{
		return _description;
	}
	

	public static class MetaReferenceTypeReader
	implements TypeReader,TypeBound
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaReference.class );
		
		public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
		throws TypeException 
		{
			_reader.bind(library, definition, typeName, typeId);
		}
		
	    public Object read(TypeInputStream in) throws TypeException, IOException
	    {
	    	
			// Use the Automatic reader to read and create this object.
			TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaReference ref = (MetaReference) reader.read( in );
			
			// Check that what its referencing exists and convert from
			// external mapping to internal mapping.
			ReferenceTypeMap mapCore = (ReferenceTypeMap) in.getTypeMap();
						
			if (  mapCore.referenceMap().isValid( ref.getType() ) )
				ref.setType( mapCore.referenceMap().getSystemId( ref.getType () ));
			else
				throw new TypeException( "TypeReference: invalid id " + ref.getType() );

			return ref;
	    }
	}
	
	public static class MetaReferenceTypeWriter
	implements TypeWriter
	{
	    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
	    {
			MetaReference tr = (MetaReference) o;
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			int id = mapCore.referenceMap().getId( tr._type );
			out.writeObject( "u16", new Integer( id ));
			out.writeObject( "meta.name", tr._description );
	    } 
	}
	
    public TypeWriter getWriter(TypeMap map) 
    throws TypeException
    {
    	return map.getWriter( map.getId(_type));
    }

    public TypeReader getReader(TypeMap map) 
    throws TypeException
    {
    	return map.getReader( map.getId(_type));
    }

}
