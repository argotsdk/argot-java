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

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;
import com.argot.common.UInt16;


/**
 * This is an abstract type.  Each system will implement its
 * own type of object location description.
 * 
 */

public class MetaObject 
{
	public static final String TYPENAME = "remote.object";

	private MetaLocation _location;
	private int _type;
	
	public MetaObject( MetaLocation location, int type )
	{
		_location = location;
		_type = type;
	}
	
	public MetaLocation getLocation()
	{
		return _location;
	}
	
	public int getType()
	{
		return _type;
	}
	
	public static class MetaObjectReader
	implements TypeReader
	{
		public Object read(TypeInputStream in) 
		throws TypeException, IOException 
		{
			MetaLocation location = (MetaLocation) in.readObject(MetaLocation.TYPENAME);
			Integer id = (Integer) in.readObject(UInt16.TYPENAME );
			int sysId = in.getTypeMap().getSystemId( id.intValue() );
			return new MetaObject( location, sysId );
		}
	}

	public static class MetaObjectWriter
	implements TypeWriter
	{
		public void write(TypeOutputStream out, Object o) 
		throws TypeException, IOException 
		{
			MetaObject obj = (MetaObject) o;
			
			out.writeObject( MetaLocation.TYPENAME, obj.getLocation() );
			int mapId = out.getTypeMap().getId( obj.getType() );
			out.writeObject(  UInt16.TYPENAME, new Integer( mapId ) );
		}
	}
}
