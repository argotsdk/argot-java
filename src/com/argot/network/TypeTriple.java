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
package com.argot.network;

import com.argot.TypeException;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryRelation;

public class TypeTriple 
{
	private long _id;
	private TypeLocation _location;
	private byte[] _definition;
	
	public TypeTriple( long id, TypeLocation location, byte[] definition )
	{
		_id = id;
		_location = location;
		_definition = definition;
	}
	
	public long getId()
	{
		return _id;
	}
	
	public TypeLocation getLocation()
	{
		return _location;
	}
	
	public byte[] getDefinition()
	{
		return _definition;
	}
	
	public static TypeLocation fixLocation( TypeLocation location, TypeMap map ) 
	throws TypeException
	{
		if (location instanceof DictionaryDefinition)
		{
			DictionaryDefinition definition = (DictionaryDefinition) location;
			definition.setId( map.getLibrary().getTypeId( definition.getName() ));
		}
		else if (location instanceof DictionaryRelation)
		{
			DictionaryRelation relation = (DictionaryRelation) location;
			relation.setId( map.getDefinitionId(relation.getId()));
		}
		return location;
		
	}
}
