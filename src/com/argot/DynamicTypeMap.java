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
package com.argot;

public class DynamicTypeMap 
extends ReferenceTypeMap
{
	private int _lastMapId;

	public DynamicTypeMap( TypeLibrary library, TypeMap refMap )
	{
		super( library, refMap );
		_lastMapId = 1;
	}
	
	public DynamicTypeMap( TypeLibrary library )
	{
		super( library, null );
		setReferenceMap(this);
		_lastMapId = 1;
	}
	
	private int getNextId()
	{
		int id;
		
		while( true )
		{
			id = _lastMapId;
			_lastMapId++;

			try 
			{
				this.getSystemId( id );
			} 
			catch (TypeException e) 
			{
				// type wasn't found to be mapped.  This one is ok.
				break;
			}

		}
		
		return id;
	}

	public void map(int id, int systemId) 
	throws TypeException
	{
		super.map(id, systemId);
	}
	
	public int getId(String name)
	throws TypeException
	{
		
		try
		{ 
			return super.getId(name);
		}
		catch( TypeException ex )
		{		
			try 
			{
				int id = getNextId();
				this.map( id, getLibrary().getId( name ) );
				return id;
			} 
			catch (TypeException e) 
			{
				throw e;
			}
		}
	}

	public int getId(Class clss) 
	throws TypeException 
	{
		int systemid = this.getLibrary().getId( clss );
		
		try
		{
			return this.getId( systemid );
		}
		catch( TypeException ex )
		{
			// not mapped yet.
			int id = getNextId();
			this.map( id, systemid );
			return id;			
		}
		
	}

    public TypeWriter getWriter(int id) throws TypeException
    {
        return super.getWriter(id);
    }

    public int getId(int systemid) throws TypeException
    {
        try
        {
            return super.getId(systemid);
        }
        catch (TypeException e)
        {
            return registerDynamic( systemid );
        }
    }

	private int registerDynamic( int systemid ) throws TypeException
	{
		int id = getNextId();
		this.map( id, systemid );
		return id;			
	}

}
