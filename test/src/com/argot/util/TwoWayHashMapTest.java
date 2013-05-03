package com.argot.util;

import junit.framework.TestCase;

public class TwoWayHashMapTest
extends TestCase
{

	
	public void testAddItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 1, 1, new Integer(1));
	}

	public void testAddDuplicateKeyItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 1, 1, new Integer(1));
		try
		{
			map.add( 1, 2, new Integer(1));
			fail("should throw exception");
		}
		catch (IllegalArgumentException e)
		{
			//ignore.
		}
	}

	public void testAddNullItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		try
		{
			map.add( 2, 1, null);
			fail("should throw exception");
		}
		catch (IllegalArgumentException e)
		{
			//ignore.
		}
	}
		
	public void testGetItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 100, 50, new Integer(1));
		int x = map.findValue(100);
		assertEquals( x, 50 );
	}
	
	public void testGetReverseItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 100, 50, new Integer(1));
		int x = map.findKey(100);
		assertEquals(x,50);
	}
	
	public void testRemoveItem()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 100, 50, new Integer(1));
		map.remove( 100 );
		Object o = map.getObjectFromKey(100);
		assertEquals(o, new Integer(1));
	}

	public void testRemoveInvalid()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 100, 50, new Integer(1));
		// returns null if its invalid.
		map.remove( 10 );		
	}
	
	public void testGetNotFound()
	{
		TwoWayHashMap map = new TwoWayHashMap();
		map.add( 100, 50, new Integer(1));
		int x = map.findValue(10);
		assertEquals(x,-1);
	}
}
