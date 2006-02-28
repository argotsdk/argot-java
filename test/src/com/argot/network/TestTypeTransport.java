package com.argot.network;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class TestTypeTransport
implements TypeTransport
{
	TypeServer _server;
	
	public TestTypeTransport( TypeServer server )
	{
		_server = server;
	}

	public TypeEndPoint openLink()
	throws IOException
	{	
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
