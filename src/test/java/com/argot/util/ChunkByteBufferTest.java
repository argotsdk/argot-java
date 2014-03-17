package com.argot.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import junit.framework.TestCase;

public class ChunkByteBufferTest
extends TestCase
{
	
	public void testGetInputStream() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		try
		{
			buffer.getInputStream();
			fail("input before output");
		}
		catch (IOException e)
		{
			// ignore
		}
	}
	
	public void testGetOutputStream() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		assertNotNull(out);
	}

	public void testGetOutputSecond() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		assertNotNull(out);
		try
		{
			buffer.getOutputStream();
			fail("should fail");
		}
		catch (IOException e)
		{
			// ignore
		}

	}
	
	public void testWriteOut() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		out.write("some data".getBytes());
	}

	public void testWriteSingleChar() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		out.write(56);
	}
	
	public void testWriteAfterClose() throws Exception
	{
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		out.write("some data".getBytes());
		out.close();
		try
		{
			out.write("some data".getBytes());
			fail("write after close");
		}
		catch (IOException e)
		{
			// ignore
		}
	}
	
	public void testWriteAndRead() throws Exception
	{
		String test = "some data";
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		out.write(test.getBytes());
		out.close();
		
		byte[] data = new byte[100];
		InputStream in = buffer.getInputStream();
		int r = in.read(data);
		assertEquals( new String( data, 0, r), test);
	}
	
	public void testWriteAndReadSingleChar() throws Exception
	{
		String test = "some data";
		ChunkByteBuffer buffer = new ChunkByteBuffer();
		OutputStream out = buffer.getOutputStream();
		out.write(test.getBytes());
		out.close();

		InputStream in = buffer.getInputStream();
		int r = in.read();
		assertEquals( r, test.charAt(0));
	}
}
