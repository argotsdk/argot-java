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
package com.argot.model;

import java.io.IOException;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExpressionReader;
import com.argot.meta.MetaExpressionResolver;
import com.argot.meta.MetaExpressionWriter;
import com.argot.meta.MetaReference;

public class ReferenceData 
extends ModelData
{
	private MetaReference _reference;
	private Object _data;
	
	public ReferenceData( MetaReference reference, Object data )
	{
		_reference = reference;
		_data = data;
	}
	
	public MetaExpression getStructure() 
	{
		return _reference;
	}
	
	public MetaReference getReference()
	{
		return _reference;
	}
	
	public Object getData()
	{
		return _data;
	}
	
	public static class ReferenceDataExpressionReader
	implements MetaExpressionReader
	{
		public TypeReader getExpressionReader(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataReader(metaReference, map.getReader( map.getStreamId(metaReference.getType())));
		}		
	}
	
	public static class ReferenceDataReader
	implements TypeReader
	{
		MetaReference _reference;
		TypeReader _reader;
		
		public ReferenceDataReader(MetaReference reference, TypeReader reader)
		{
			_reference = reference;
			_reader = reader;
		}
		
		public Object read(TypeInputStream in) 
		throws TypeException,IOException 
		{
			//return new ReferenceData( _reference, _reader.read(in));
			return _reader.read(in);
		}	
	}

	public static class ReferenceDataExpressionWriter
	implements MetaExpressionWriter
	{
		public TypeWriter getExpressionWriter(TypeMap map, MetaExpressionResolver resolver, TypeElement element)
		throws TypeException 
		{
			MetaReference metaReference = (MetaReference) element;
			return new ReferenceDataWriter(map.getWriter( map.getStreamId(metaReference.getType())));		
		}
	}
	
	private static class ReferenceDataWriter
	implements TypeWriter
	{
		private TypeWriter _writer;
		
		public ReferenceDataWriter(TypeWriter type )
		{
			_writer = type;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if (!(o instanceof ReferenceData))
			{
				throw new TypeException("bad data");
			}
			ReferenceData data = (ReferenceData) o;
			//System.out.println("Reference Type:" + out.getTypeMap().getLibrary().getName( data.getReference().getType()) + " " + data.getData().getClass().getName() );
			_writer.write(out, data.getData());
		}
	}
}
