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
package com.argot.remote;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;
import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.BigEndianUnsignedShort;
import com.argot.common.U8Ascii;
import com.argot.meta.MetaBase;
import com.argot.meta.MetaDefinition;

public class MetaMethod 
extends MetaBase
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

	public MetaMethod( int interfaceId, String name, Object[] requestTypes, Object[] responseTypes, Object[] errorTypes )
	{
		_interfaceId = interfaceId;
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
	
	public MetaMethod( Integer interfaceType, String name, List requestList, List responseList, List errorTypes )
	{
		this( interfaceType.intValue(), name, requestList.toArray(), responseList.toArray(), errorTypes.toArray() );
	}
	
	public MetaMethod( int interfaceId, String name, MetaParameter[] requestTypes, MetaParameter[] responseTypes, int[] errorTypes )
	{
		_interfaceId = interfaceId;
		_name = name;
		_requestTypes = requestTypes;
		_responseTypes = responseTypes;
		_errorTypes = errorTypes;
	}
	

	public void bind(TypeLibrary library, TypeElement definition, String memberTypeName, int memberTypeId) 
	throws TypeException 
	{
		super.bind(library, definition, memberTypeName, memberTypeId);
		
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
	
	public Method getNativeMethod()
	{
		return _nativeMethod;
	}

	public static class MetaMethodReader
	implements TypeReader
	{
		public Object read(TypeInputStream in, TypeElement element) 
		throws TypeException, IOException 
		{
			TypeReader reader = new TypeReaderAuto( MetaMethod.class );
			MetaMethod mm = (MetaMethod) reader.read( in, element );
			mm._interfaceId = in.getTypeMap().getSystemId( mm._interfaceId );
			for ( int x=0 ; x< mm._errorTypes.length ; x++ )
			{
				mm._errorTypes[x] = in.getTypeMap().getSystemId( mm._errorTypes[x] );
			}
			return mm;
		}
	}
	
	public static class MetaMethodWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o, TypeElement element) 
		throws TypeException, IOException 
		{
			MetaMethod mm = (MetaMethod) o;
			int x;
			
			// write interface id.
			int id = out.getTypeMap().getId( mm.getInterfaceType() );
			out.writeObject( BigEndianUnsignedShort.TYPENAME, new Integer(id) );
			out.writeObject( U8Ascii.TYPENAME, mm.getMethodName() );
			
			if ( mm.getRequestTypes() != null )
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer( mm.getRequestTypes().length ));
				for( x=0 ;x < mm.getRequestTypes().length; x++ )
				{
					//id = out.getTypeMap().getId( mm.getRequestTypes()[x].getParamType() );
					//out.writeObject( BigEndianUnsignedShort.TYPENAME, new Integer(id) );
					out.writeObject( MetaParameter.TYPENAME, mm.getRequestTypes()[x] );
				}
			}
			else
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer(0));
			}
	
			if ( mm.getResponseTypes() != null )
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer( mm.getResponseTypes().length ));
				for( x=0 ;x < mm.getResponseTypes().length; x++ )
				{
					//id = out.getTypeMap().getId( mm.getResponseTypes()[x].getParamType() );
					//out.writeObject( BigEndianUnsignedShort.TYPENAME, new Integer(id) );
					out.writeObject( MetaParameter.TYPENAME, mm.getResponseTypes()[x] );
				}
			}
			else
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer(0));
			}
	
			if ( mm.getErrorTypes() != null )
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer( mm.getErrorTypes().length ));
				for( x=0 ;x < mm.getErrorTypes().length; x++ )
				{
					id = out.getTypeMap().getId( mm.getErrorTypes()[x]);
					out.writeObject( BigEndianUnsignedShort.TYPENAME, new Integer(id) );
				}
			}
			else
			{
				out.writeObject( BigEndianUnsignedByte.TYPENAME, new Integer(0));
			}
		}
	}

	
}
