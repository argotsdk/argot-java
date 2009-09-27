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
package com.argot.remote;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import com.argot.ReferenceTypeMap;
import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeLocationRelation;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.auto.TypeReaderAuto;
import com.argot.common.U8Ascii;
import com.argot.common.UInt16;
import com.argot.common.UInt8;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaDefinition;
import com.argot.meta.MetaExpression;

public class MetaMethod 
extends MetaExpression
implements MetaDefinition
{
	private int _interfaceId;
	private String _name;
	private MetaParameter[] _requestTypes;
	private MetaParameter[] _responseTypes;
	private int[] _errorTypes;
	
	private Method _nativeMethod;
	private MetaInterface _metaInterface;
	
	
	public static final String TYPENAME = "remote.method";
	public static final String VERSION = "1.3";

	public MetaMethod( String name, Object[] requestTypes, Object[] responseTypes, Object[] errorTypes )
	{
		_name = name;
		
		if ( requestTypes != null )
		{
			_requestTypes = new MetaParameter[ requestTypes.length ];
			for ( int x = 0 ; x< requestTypes.length; x++ )
			{
				_requestTypes[x] = (MetaParameter) requestTypes[x];
			}
		}
		else
		{
			_requestTypes = new MetaParameter[0];
		}
		
		if ( responseTypes != null )
		{
			_responseTypes = new MetaParameter[ responseTypes.length ];
			for ( int x = 0 ; x< responseTypes.length; x++ )
			{				
				_responseTypes[x] = (MetaParameter) responseTypes[x];			
			}
		}
		else
		{
			_responseTypes = new MetaParameter[0];
		}

		if ( errorTypes != null )
		{
			_errorTypes = new int[ errorTypes.length ];
			for ( int x = 0 ; x< errorTypes.length; x++ )
			{
				_errorTypes[x] = ((Integer) errorTypes[x]).intValue();
			}
		}
		else
		{
			_errorTypes = new int[0];
		}
				
	}
	
	public MetaMethod( String name, List requestList, List responseList, List errorTypes )
	{
		this( name, requestList.toArray(), responseList.toArray(), errorTypes.toArray() );
	}
	
	public MetaMethod( String name, MetaParameter[] requestTypes, MetaParameter[] responseTypes, int[] errorTypes )
	{
		_name = name;
		_requestTypes = requestTypes;
		_responseTypes = responseTypes;
		_errorTypes = errorTypes;
	}
	

	public void bind(TypeLibrary library, int memberTypeId, TypeLocation location, TypeElement definition) 
	throws TypeException 
	{
		super.bind(library, memberTypeId, location, definition);
		
		_interfaceId = ((TypeLocationRelation)location).getId();
		
		TypeElement structure = library.getStructure( _interfaceId );
		if ( !(structure instanceof MetaInterface))
			throw new TypeException("Interface Id not Interface for Method");
		
		_metaInterface = (MetaInterface) structure;
		_metaInterface.addMethod( this );
	}
	
	public String getTypeName() 
	{
		return TYPENAME;
	}
	
	public int getInterfaceType()
	{
		return _interfaceId;
	}
	
	public MetaInterface getMetaInterface()
	{
		return _metaInterface;
	}
	
	public String getMethodName()
	{
		return _name;
	}
	
	public Method getMethod() 
	{
		return getMetaInterface().getMethod(this);
	}
	
	public MetaParameter[] getRequestTypes() 
	{
		return _requestTypes;
	}

	public MetaParameter[] getResponseTypes() 
	{
		return _responseTypes;
	}
	
	public int[] getErrorTypes()
	{
		return _errorTypes;
	}

	public int getMatchingErrorType(Throwable exception)
	{
		try 
		{
			int ids[] = getLibrary().getId(exception.getClass());
			if (ids.length != 1)
			{
				if (ids.length>1)
					throw new TypeException("Class bound to multiple system types:" +exception.getClass().getName());
				if (ids.length==0)
					throw new TypeException("Class not bound to any mapped type:"+exception.getClass().getName());
			}
			
			// see if the exception is directly related.
			for (int x=0;x<_errorTypes.length;x++)
			{
				if (ids[0] == _errorTypes[x])
					return ids[0];
			}
			
			// look through abstract types.
			for (int x=0;x<_errorTypes.length;x++)
			{
				TypeElement element = getLibrary().getStructure(_errorTypes[x]);
				if (element instanceof MetaAbstract)
				{
					MetaAbstract metaAbstract = (MetaAbstract) element;
					if (metaAbstract.isMapped(ids[0]))
					{
						return _errorTypes[x];
					}
				}
			}
		} catch (TypeException e) {
			return TypeLibrary.NOTYPE;
		}
		return TypeLibrary.NOTYPE;
	}
	
	public Method getNativeMethod()
	{
		return _nativeMethod;
	}

	public static class MetaMethodReader
	implements TypeReader,TypeBound
	{
		TypeReaderAuto _reader = new TypeReaderAuto( MetaMethod.class );
		
		public void bind(TypeLibrary library, int definitionId, TypeElement definition) 
		throws TypeException 
		{
			_reader.bind(library, definitionId, definition);
		}
		
		public Object read(TypeInputStream in) 
		throws TypeException, IOException 
		{
			ReferenceTypeMap mapCore = (ReferenceTypeMap) in.getTypeMap();
			
			TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaMethod mm = (MetaMethod) reader.read( in );
			mm._interfaceId = mapCore.referenceMap().getDefinitionId( mm._interfaceId );
			for ( int x=0 ; x< mm._errorTypes.length ; x++ )
			{
				mm._errorTypes[x] = mapCore.referenceMap().getDefinitionId( mm._errorTypes[x] );
			}
			return mm;
		}
	}
	
	public static class MetaMethodWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MetaMethod mm = (MetaMethod) o;
			int x;
			
			ReferenceTypeMap mapCore = (ReferenceTypeMap) out.getTypeMap();
			
			// write interface id.
			int id = mapCore.referenceMap().getStreamId( mm.getInterfaceType() );
			out.writeObject( U8Ascii.TYPENAME, mm.getMethodName() );
			
			if ( mm.getRequestTypes() != null )
			{
				out.writeObject( UInt8.TYPENAME, new Integer( mm.getRequestTypes().length ));
				for( x=0 ;x < mm.getRequestTypes().length; x++ )
				{
					out.writeObject( MetaParameter.TYPENAME, mm.getRequestTypes()[x] );
				}
			}
			else
			{
				out.writeObject( UInt8.TYPENAME, new Integer(0));
			}
	
			if ( mm.getResponseTypes() != null )
			{
				out.writeObject( UInt8.TYPENAME, new Integer( mm.getResponseTypes().length ));
				for( x=0 ;x < mm.getResponseTypes().length; x++ )
				{
					out.writeObject( MetaParameter.TYPENAME, mm.getResponseTypes()[x] );
				}
			}
			else
			{
				out.writeObject( UInt8.TYPENAME, new Integer(0));
			}
	
			if ( mm.getErrorTypes() != null )
			{
				out.writeObject( UInt8.TYPENAME, new Integer( mm.getErrorTypes().length ));
				for( x=0 ;x < mm.getErrorTypes().length; x++ )
				{
					id = mapCore.referenceMap().getStreamId( mm.getErrorTypes()[x]);
					out.writeObject( UInt16.TYPENAME, new Integer(id) );
				}
			}
			else
			{
				out.writeObject( UInt8.TYPENAME, new Integer(0));
			}
		}
	}

	
}
