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

import java.io.IOException;
import java.util.Arrays;
import java.util.Stack;

import com.argot.ReferenceTypeMap;
import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeLibrary;
import com.argot.TypeMapCore;
import com.argot.dictionary.Dictionary;


public class DynamicClientTypeMap 
extends ReferenceTypeMap
{
	private TypeClient _typeClient;
	
	// This resolveStack is used to look for ensuring that we don't
	// end up in an endless loop trying to resolve self referenced
	// or looping type definitions.
	
	private Stack _resolveStack;
	
	private boolean _metaDictionaryChecked;
	private boolean _metaDictionaryOk;

	public DynamicClientTypeMap( TypeLibrary library, TypeClient client ) 
	throws TypeException, IOException
	{
		super( library, null );
		setReferenceMap( this );

		_typeClient = client;
		_resolveStack = new Stack();		

		_metaDictionaryChecked = false;
		_metaDictionaryOk = false;
	}
	
	private void checkMetaDictionary() 
	throws TypeException
	{
		if ( !_metaDictionaryChecked )
		{
			try
			{
				TypeLibrary library = getLibrary();
				
				// The base types must be the same as the MetaDictionary core.
				// Wait until first request before mapping.  Stops ghosting.
				TypeMapCore.mapMeta( this, library );
				map( 22, library.getId("dictionary.map"));
				map( 23, library.getId("dictionary.words"));
				map( 24, library.getId("dictionary.definition"));
				map( 25, library.getId("dictionary.entry"));	
				map( 26, library.getId("meta.envelop"));
				map( 27, library.getId("meta.definition#envelop"));		

				byte[] localMetaDictionary = Dictionary.writeCore( this );
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
		
		// See if the type has already been mapped.
		int x;
		
		try
		{ 
			x = super.getId( systemId );
		}
		catch( TypeException ex )
		{
			x = NOTYPE;
		}
		
		if ( x != NOTYPE )
			return x;
			
		// No mapping..  Map this type.
		// Requires the Name and structure.
		// The server has the definative map.  So we send
		// the details over and expect a response of the id
		// to use.
		String name = getLibrary().getName( systemId );
		
		if ( _resolveStack.contains( name ))
		{
			// If we have a self referenced type we first need to get
			// and ID for the Dynamic type map.  From that we can then
			// check the full definition because we can describe it.

			try 
			{
				x = _typeClient.reserveType( name );
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
			_resolveStack.push( name );
		
			// As we write out the definition of this type we may
			// come accross other types that must be resolved.  This
			// will recurively resolve each type.

			byte[] definition = TypeHelper.toByteArray( this, getLibrary().getStructure( systemId ));
			
			try 
			{
				x = _typeClient.resolveType( name, definition );
			} 
			catch (IOException e) 
			{
				throw new TypeException( e.getMessage() );
			}
			
			_resolveStack.pop();
		}
		
		if ( x == NOTYPE )
		{
			throw new TypeException( "unknown type on server" + name );
		}

		map( x, systemId );

		return x;
	}
	
	private void resolveReverse( int id )
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
		
		int systemid = getLibrary().getId( typeInfo.getName() );

		byte[] definition = TypeHelper.toByteArray( this, getLibrary().getStructure( systemid ) );

		if ( Arrays.equals( definition, typeInfo.getDefinition() ) )
		{
			// The definition matches..  so it is ok.  Map it.
			map( id, systemid );
			return;
		}
		
		throw new TypeException( "definition do not match");
	}
	

	public int getId(int systemId) 
	throws TypeException 
	{
		try
		{
			return super.getId( systemId );
		}
		catch( TypeException ex )
		{
			return resolveType( systemId );
		}
		
	}

	public int getId(String name) 
	throws TypeException
	{
		try
		{
			return super.getId( name );
		}
		catch( TypeException ex )
		{
			try
			{
				int id = resolveType( getLibrary().getId( name ) );
				if ( id == NOTYPE)
					throw new TypeException("not mapped");
				return id;
			}
			catch( TypeException ex2 )
			{
				throw ex2;
			}
		}
	}

	public int getSystemId(int id) 
	throws TypeException 
	{
		try
		{
			// if successful it will just return.
			return  super.getSystemId( id );
		}
		catch( TypeException ex )
		{
			resolveReverse( id );
			return getSystemId( id );
		}		
	}

}
