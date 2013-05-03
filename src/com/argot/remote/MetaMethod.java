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
package com.argot.remote;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

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
	
	public MetaMethod( String name, List<MetaParameter> requestList, List<MetaParameter> responseList, List<Integer> errorTypes )
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
			TypeMap refMap = (TypeMap) in.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
			
			TypeReader reader = _reader.getReader(in.getTypeMap());
			MetaMethod mm = (MetaMethod) reader.read( in );
			mm._interfaceId = refMap.getDefinitionId( mm._interfaceId );
			for ( int x=0 ; x< mm._errorTypes.length ; x++ )
			{
				mm._errorTypes[x] = refMap.getDefinitionId( mm._errorTypes[x] );
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
			
			TypeMap refMap = (TypeMap) out.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
			
			// write interface id.
			int id = refMap.getStreamId( mm.getInterfaceType() );
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
					id = refMap.getStreamId( mm.getErrorTypes()[x]);
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
