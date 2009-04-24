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

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import com.argot.ReferenceTypeMap;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeLocationDefinition;
import com.argot.TypeMap;
import com.argot.TypeMapper;
import com.argot.dictionary.Dictionary;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryRelation;


public class DynamicClientTypeMapper 
implements TypeMapper
{
	private TypeClient _typeClient;
	private ReferenceTypeMap _map;
	private TypeLibrary _library;
	
	// This resolveStack is used to look for ensuring that we don't
	// end up in an endless loop trying to resolve self referenced
	// or looping type definitions.
	
	private Stack _resolveStack;
	
	private boolean _metaDictionaryChecked;
	private boolean _metaDictionaryOk;

	public DynamicClientTypeMapper( TypeClient client ) 
	throws TypeException, IOException
	{
		_typeClient = client;
		_resolveStack = new Stack();		

		_metaDictionaryChecked = false;
		_metaDictionaryOk = false;
	}

	public void initialise(TypeMap map) 
	throws TypeException 
	{
		_map = (ReferenceTypeMap) map;
		_library = map.getLibrary();
		_typeClient.initialise(map);
	}
	
	private void checkMetaDictionary() 
	throws TypeException
	{
		if ( !_metaDictionaryChecked )
		{
			try
			{				
				// The base types must be the same as the MetaDictionary core.
				// Wait until first request before mapping.  Stops ghosting.
				
				byte[] localMetaDictionary = Dictionary.writeCore( _map );
				_metaDictionaryOk = _typeClient.checkMetaDictionary( localMetaDictionary );
				_metaDictionaryChecked = true;				
			}
			catch (IOException e)
			{
				throw new TypeException( "Failed to check Meta Dictionary", e );
			}		
			
		}
		
		if ( !_metaDictionaryOk )
		{
			throw new TypeException( "Client and Server Meta Dictionaries do not match");
		}
	}

	private int resolveType( int systemId )
	throws TypeException
	{
		checkMetaDictionary();
		
		int x;
				
		// No mapping..  Map this type.
		// Requires the Name and structure.
		// The server has the definative map.  So we send
		// the details over and expect a response of the id
		// to use.
		TypeLocation location = _library.getLocation( systemId );
		
		if ( _resolveStack.contains( location ))
		{
			// If we have a self referenced type we first need to get
			// and ID for the Dynamic type map.  From that we can then
			// check the full definition because we can describe it.

			try 
			{
				x = _typeClient.reserveType( location );
			} 
			catch (IOException e) 
			{
				throw new TypeException( e.getMessage() );
			} 

		}
		else
		{
			// We haven't resolved the type yet so put it on the stack
			// and resolve it.
			_resolveStack.push( location );
		
			// As we write out the definition of this type we may
			// come accross other types that must be resolved.  This
			// will recurively resolve each type.

			byte[] definition = TypeHelper.toByteArray( _map, _library.getStructure( systemId ));
			
			try 
			{
				x = _typeClient.resolveType( location, definition );
			} 
			catch (IOException e) 
			{
				throw new TypeException( e.getMessage() );
			}
			
			_resolveStack.pop();
		}
		
		if ( x == TypeMap.NOTYPE )
		{
			throw new TypeException( "unknown type on server" );
		}

		return x;
	}
	
	private TypeTriple resolveDefault( TypeLocation location )
	throws TypeException
	{
		checkMetaDictionary();
		
		TypeTriple typeInfo;
		try 
		{
			typeInfo = _typeClient.resolveDefault(location);
		} 
		catch (IOException e) 
		{
			throw new TypeException("resolve default failed", e);
		}

		int systemid = _library.getTypeId( typeInfo.getLocation() );

		byte[] definition = TypeHelper.toByteArray( _map, _library.getStructure( systemid ) );

		if ( Arrays.equals( definition, typeInfo.getDefinition() ) )
		{
			// The definition matches..  so it is ok.  Map it.
			return typeInfo;
		}
		
		throw new TypeException( "definition do not match");

	}
	
	private int resolveReverse( int id )
	throws TypeException
	{
		checkMetaDictionary();
		
		TypeTriple typeInfo;

		try {
			typeInfo = _typeClient.resolveReverse(id);
		} 
		catch (IOException e) 
		{
			throw new TypeException( e.getMessage() );	
		} 
		
		
		int systemid = _library.getTypeId( typeInfo.getLocation() );

		byte[] definition = TypeHelper.toByteArray( _map, _library.getStructure( systemid ) );

		if ( Arrays.equals( definition, typeInfo.getDefinition() ) )
		{
			// The definition matches..  so it is ok.  Map it.
			return systemid;
		}
		
		throw new TypeException( "definition do not match");
	}
	

	public int map(TypeLocation location) 
	throws TypeException
	{
		try
		{
			int id = resolveType( _library.getTypeId( location ) );
			if ( id == TypeMap.NOTYPE)
				throw new TypeException("not mapped");
			return id;
		}
		catch( TypeException ex2 )
		{
			throw ex2;
		}
	}


	public int map(int definitionId) 
	throws TypeException 
	{
		int id = resolveType(definitionId);
		_map.map(id, definitionId);
		//System.err.println("client mapping: " + id + " to " + definitionId + "(" + _library.getName(definitionId)+")");
		return id;
	}

	public int mapReverse(int streamId) 
	throws TypeException 
	{
		int id = resolveReverse(streamId);
		_map.map(streamId, id);
		return id;
	}

	public int mapDefault(int nameId) 
	throws TypeException 
	{
		TypeTriple typeInfo = resolveDefault(_library.getLocation(nameId));
		int definitionId = _library.getTypeId(typeInfo.getLocation());
		_map.map((int)typeInfo.getId(), definitionId);
		return (int) typeInfo.getId();
	}

}
