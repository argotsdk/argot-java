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

public class TypeDefaultMapping 
{
	private int _mapId;
	private int _systemId;
	private TypeTriple _definition;
	
	public TypeDefaultMapping( int mapId, TypeTriple definition )
	{
		_mapId = mapId;
		_definition = definition;
	}
	
	public void setMapId(int _mapId) 
	{
		this._mapId = _mapId;
	}
	
	public int getMapId() 
	{
		return _mapId;
	}

	public void setDefinition(TypeTriple _definition) 
	{
		this._definition = _definition;
	}

	public TypeTriple getDefinition() 
	{
		return _definition;
	}

	public void setSystemId(int _systemId) 
	{
		this._systemId = _systemId;
	}

	public int getSystemId() 
	{
		return _systemId;
	}
	
}
