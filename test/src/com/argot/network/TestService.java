package com.argot.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TestService
implements TypeLink
{
	/*
	 * A very simple echo service.  Only echos the
	 * first 100 characters.
	 */
	public void processMessage( TypeEndPoint connection ) 
	throws IOException
	{
		InputStream in = connection.getInputStream();
		byte[] buffer = new byte[100];
		int read = in.read( buffer );
		OutputStream out = connection.getOutputStream();
		out.write( buffer, 0, read );
	}

}
