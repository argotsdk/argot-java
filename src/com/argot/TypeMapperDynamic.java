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
package com.argot;

import java.util.Iterator;

import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaVersion;

public class TypeMapperDynamic 
implements TypeMapper 
{
	private int _lastMapId;
	private TypeMap _map;
	private TypeMapper _chain;
	private TypeLibrary _library;
	
	public TypeMapperDynamic(TypeMapper chain)
	{
		_lastMapId = 0;
		_chain = chain;
	}

	private int getNextId() 
	{
		int id;

		while (true) 
		{
			id = _lastMapId;
			_lastMapId++;

			if (!_map.isValid(id))
			{
				// type wasn't found to be mapped. This one is ok.
				break;
			}
		}
		return id;
	}

	public void initialise(TypeMap map) 
	throws TypeException 
	{
		_map = map;
		_library = _map.getLibrary();
		_chain.initialise(map);
	}

	public int map(int definitionId) 
	throws TypeException 
	{
		TypeLocation location = _library.getLocation( definitionId );
		if (location instanceof TypeLocationName)
		{
			return this.mapDefault(definitionId);
		}
		
		int mapId = getNextId();
		_map.map(mapId, definitionId);
		return mapId;
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		return _chain.mapReverse(streamId);
	}

	public int mapDefault(int systemNameId) 
	throws TypeException 
	{		
		TypeElement elemStructure = _library.getStructure(systemNameId);
		if (!(elemStructure instanceof MetaIdentity))
			throw new TypeException("MapDefault requires name definition: " + elemStructure.getClass().getName() );
		
		MetaName name = _library.getName(systemNameId);
		
		MetaIdentity metaName = (MetaIdentity) elemStructure;
		Iterator iter = metaName.getVersions().iterator();
		if (!iter.hasNext())
			throw new TypeException("Type has no versions " + name  );
		
		MetaVersion version = (MetaVersion) iter.next();
		int verId = _library.getDefinitionId(name, version);
		
		int mapId = getNextId();
		_map.map(mapId, verId);
		return mapId;
	}

}
