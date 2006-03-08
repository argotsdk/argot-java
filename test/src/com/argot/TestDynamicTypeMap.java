package com.argot;

import com.argot.meta.MetaSequence;

import junit.framework.TestCase;

public class TestDynamicTypeMap
extends TestCase
{
	TypeLibrary _library;
	DynamicTypeMap _typeMap;
	
	protected void setUp() 
	throws Exception
	{
		super.setUp();
		
		_library = TypeLibrarySingleton.getDefault();
		_typeMap = new DynamicTypeMap( _library );
	}
	
	public void testMap()
	throws Exception
	{
		_typeMap.map( 1, _library.getId( "u8" ));
	}
	
	public void testGetIdName()
	throws Exception
	{
		int id = _typeMap.getId( "u8" );
		assertTrue( id != TypeMap.NOTYPE );
	}
	
	public void testGetIdSystemId()
	throws Exception
	{
		int id = _typeMap.getId( _library.getId("u8") );
		assertTrue( id != TypeMap.NOTYPE );
	}
	
	public void testGetIdClass()
	throws Exception
	{
		int id = _typeMap.getId( MetaSequence.class );
		assertTrue( id != TypeMap.NOTYPE );
	}
	
	public void testGetReader()
	throws Exception
	{
		TypeReader reader = _typeMap.getReader( _typeMap.getId("u8"));
		assertNotNull( reader );
	}
	
	public void testGetWriter()
	throws Exception
	{
		TypeWriter writer = _typeMap.getWriter( _typeMap.getId("u8"));
		assertNotNull(writer);
	}
	
	public void testIsValidName()
	throws Exception
	{
		boolean valid = _typeMap.isValid( "u8" );
		assertTrue( valid );
	}

}
