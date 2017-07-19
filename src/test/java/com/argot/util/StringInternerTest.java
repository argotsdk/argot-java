package com.argot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.CharBuffer;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class StringInternerTest
{

	private StringWeakInterner interner;

	@Before
	public void setup()
	{
		this.interner = StringWeakInterner.get();
	}

	@Test
	public void testIntern()
	{
		final String str = "test";
		final String result = interner.get(CharBuffer.wrap(str));
		assertNotNull(result);
		assertEquals(result, "test");
	}

	@Test
	public void testHashCollision()
	{
		final String str = "test"; // teu6
		final Random random = new Random();
		final int hash = str.hashCode();

		String result = interner.get(CharBuffer.wrap("test"));
		assertNotNull(result);
		assertEquals(result, "test");
		result = null;

		System.gc();

		result = interner.get(CharBuffer.wrap("teu6"));
		assertNotNull(result);
		assertEquals(result, "teu6");
		result = null;

		System.gc();

		result = interner.get(CharBuffer.wrap("tg5t"));
		assertNotNull(result);
		assertEquals(result, "tg5t");
		result = null;

		System.gc();

		result = interner.get(CharBuffer.wrap("test"));
		assertNotNull(result);
		assertEquals(result, "test");
		result = null;

	}

}
