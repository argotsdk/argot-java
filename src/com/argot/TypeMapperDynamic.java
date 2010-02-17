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
package com.argot;

import java.util.Iterator;

import com.argot.meta.MetaCluster;
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
		else if (location instanceof TypeLocationDefinition)
		{
			// Need to ensure the parent group is also mapped.  Getting stream should force it.
			TypeLocationDefinition tld = (TypeLocationDefinition) location;
			_map.getStreamId(tld.getName().getGroup());
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
		{	
			if (elemStructure instanceof MetaCluster)
			{
				TypeLocationName location =  (TypeLocationName) _library.getLocation(systemNameId);
				_map.getStreamId(location.getName().getGroup());
				int mapId = getNextId();
				_map.map(mapId, systemNameId);
				return mapId;				
			}
			throw new TypeException("MapDefault requires name definition: " + elemStructure.getClass().getName() );
		}
		MetaName name = _library.getName(systemNameId);
		
		// Need to ensure the parent group is also mapped.  Getting stream should force it.
		_map.getStreamId(name.getGroup());
		
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
