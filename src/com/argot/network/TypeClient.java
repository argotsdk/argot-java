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
package com.argot.network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;


import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeLibrary;
import com.argot.common.BigEndianSignedInteger;
import com.argot.common.BigEndianUnsignedByte;
import com.argot.common.U8Boolean;
import com.argot.remote.MetaObject;

public class TypeClient
{
	private TypeLink _link;
	private ProtocolTypeMap _typeMap;

	public TypeClient( TypeLibrary library, TypeLink link )
	throws TypeException
	{
		_link = link;
		_typeMap = new ProtocolTypeMap( library );
	}

	public int resolveType( String name, byte[] definition )
	throws IOException, TypeException
	{		
		// Write the name and definition to the request body.	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream tmos = new TypeOutputStream( baos, _typeMap );
		tmos.writeObject( "u8", new Short(ProtocolTypeMap.MAP) );
		tmos.writeObject( "u8ascii", name );
		tmos.writeObject( "u16binary", definition );		
		baos.close();
		
		// Send the request to the server.
		byte[] response = _link.processMessage( baos.toByteArray() );
		
		ByteArrayInputStream bais = new ByteArrayInputStream( response );
		TypeInputStream tmis = new TypeInputStream( bais, _typeMap );
		Short type = (Short) tmis.readObject( BigEndianUnsignedByte.TYPENAME );
		if ( type.intValue() != ProtocolTypeMap.MAP )throw new TypeException("Bad Protocol Error"); 
		Integer value = (Integer) tmis.readObject( BigEndianSignedInteger.TYPENAME );
		bais.close();

		return value.intValue();
	}

	public int reserveType( String name )
	throws IOException, TypeException
	{		
		// Write the name and definition to the request body.	
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream tmos = new TypeOutputStream( baos, _typeMap );
		tmos.writeObject( "u8", new Short(ProtocolTypeMap.MAPRES) );		
		tmos.writeObject( "u8ascii", name );
		baos.close();
				
		// Send the request to the server.
		byte[] response = _link.processMessage( baos.toByteArray() );
		
		ByteArrayInputStream bais = new ByteArrayInputStream( response );
		TypeInputStream tmis = new TypeInputStream( bais, _typeMap );
		Short type = (Short) tmis.readObject( BigEndianUnsignedByte.TYPENAME );
		if ( type.intValue() != ProtocolTypeMap.MAPRES )throw new TypeException("Bad Protocol Error");		
		Integer value = (Integer) tmis.readObject( BigEndianSignedInteger.TYPENAME );
		bais.close();

		return value.intValue();
	}

	public TypeTriple resolveReverse( int id )
	throws IOException, TypeException
	{		
		// Write the name and definition to the request body.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream tmos = new TypeOutputStream( baos, _typeMap );
		tmos.writeObject( "u8", new Short(ProtocolTypeMap.MAPREV) );		
		tmos.writeObject( BigEndianSignedInteger.TYPENAME, new Integer( id ) );
		baos.close();
				
		// Send the request to the server.
		byte[] response = _link.processMessage( baos.toByteArray() );
		
		ByteArrayInputStream bais = new ByteArrayInputStream( response );
		TypeInputStream tmis = new TypeInputStream( bais, _typeMap );
		Short type = (Short) tmis.readObject( BigEndianUnsignedByte.TYPENAME );
		if ( type.intValue() != ProtocolTypeMap.MAPREV )throw new TypeException("Bad Protocol Error");		
		String name = (String) tmis.readObject( "u8ascii" );
		byte[] definition = (byte[]) tmis.readObject( "u16binary" );
		bais.close();
		
		return new TypeTriple( id, name, definition );

	}
	
	public MetaObject getBaseObject( TypeMap map )
	throws IOException, TypeException
	{		
		// Write the name and definition to the request body.
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		TypeOutputStream tmos = new TypeOutputStream( baos, _typeMap );
		tmos.writeObject( "u8", new Short(ProtocolTypeMap.BASE) );		
		baos.close();
				
		// Send the request to the server.
		byte[] response = _link.processMessage( baos.toByteArray() );
		
		ByteArrayInputStream bais = new ByteArrayInputStream( response );
		TypeInputStream tmis = new TypeInputStream( bais, map );
		Short type = (Short) tmis.readObject( BigEndianUnsignedByte.TYPENAME );
		if ( type.intValue() != ProtocolTypeMap.BASE )throw new TypeException("Bad Protocol Error");		
		Boolean value = (Boolean) tmis.readObject( U8Boolean.TYPENAME );
		if ( !value.booleanValue() )
		{
			return null;
		}
		MetaObject object = (MetaObject) tmis.readObject( MetaObject.TYPENAME );
		tmis.getStream().close();
		
		return object;
	}	
}
