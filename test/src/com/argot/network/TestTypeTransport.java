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

package com.argot.network;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class TestTypeTransport
implements TypeTransport
{
	private TypeServer _server;
	private int _connectionCount;
	
	public TestTypeTransport( TypeServer server )
	{
		_server = server;
		_connectionCount = 0;
	}
	
	public int getConnectionCount()
	{
		return _connectionCount;
	}

	public TypeEndPoint openLink()
	throws IOException
	{	
		_connectionCount++;
		
		PipedInputStream _inStream = new PipedInputStream();
		PipedOutputStream _outStream = new PipedOutputStream( _inStream );
		
		PipedInputStream _inStream2 = new PipedInputStream();
		PipedOutputStream _outStream2 = new PipedOutputStream( _inStream2 );
		
		TypeEndPoint endClient = new TypeEndPointBasic( _inStream, _outStream2 );
		TypeEndPoint endServer = new TypeEndPointBasic( _inStream2, _outStream );
		
		ServerRunner runner = new ServerRunner( _server, endServer );
		Thread thread = new Thread( runner );
		thread.start();
		
		return endClient;
	}

	public void closeLink( TypeEndPoint endPoint )
	{
		
	}
	
	private class ServerRunner
	implements Runnable
	{
		TypeServer _server;
		TypeEndPoint _connection;
		
		public ServerRunner( TypeServer server, TypeEndPoint connection )
		{
			_server = server;
			_connection = connection;
		}
		
		public void run()
		{
			try
			{
				_server.processMessage( _connection );
			}
			catch (IOException e)
			{
				try
				{
					_connection.getInputStream().close();
				}
				catch (IOException e1) {}
				
				try
				{
					_connection.getOutputStream().close();
				}
				catch (IOException e1) {}
				e.printStackTrace();
			}			
		}
		
	}

}
