package com.argot.remote;

public class WrappedRemoteException
extends Throwable
{
	private static final long serialVersionUID = 1L;
	
	public static final String TYPENAME = "remote.exception.wrapped";

	// used as constructor for argot.
	public WrappedRemoteException(String message,Throwable cause)
	{
		super(message, cause);
	}
	
	// copy constructor.
	public WrappedRemoteException(Throwable original)
	{
		super(original.getClass().getName()+":"+original.getMessage(),original.getCause());
		this.setStackTrace(original.getStackTrace());
	}
}
