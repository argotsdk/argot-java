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
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.UInt16;
import com.argot.common.UVInt28;

public class MetaReference
extends MetaExpression
implements MetaDefinition
{
	public static final String TYPENAME  = "meta.reference";
	public static final String VERSION = "1.3";
	
	private int _type;

	public MetaReference( int type )
	{
		_type = type;
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

	public static class MetaReferenceTypeReader
	implements TypeReader,TypeBound,TypeLibraryReader, MetaExpressionReader
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaReference.class );
		
		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_reader.bind(library, definitionId, definition);
		}
		
		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return this;
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
				ref.setType( mapCore.referenceMap().getNameId( ref.getType() ));
			else
				throw new TypeException( "TypeReference: invalid id " + ref.getType() );

			return ref;
	    }

		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			int refNameId = map.getStreamId(metaReference._type);
			return map.getReader( refNameId );
		}
	}
	
	public static class MetaReferenceTypeWriter
	implements TypeWriter, TypeLibraryWriter, MetaExpressionWriter
	{
	    public void write(TypeOutputStream out, Object o) throws TypeException, IOException
	    {
			MetaReference tr = (MetaReference) o;
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			int id = mapCore.referenceMap().getStreamId( tr._type );
			out.writeObject( UVInt28.TYPENAME, new Integer( id ));
	    }

		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			// First map the name type.
			int refNameId = map.getStreamId(metaReference._type);
			
			// Next map the defaultId.
			return map.getWriter( refNameId );
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		} 
	}
}
