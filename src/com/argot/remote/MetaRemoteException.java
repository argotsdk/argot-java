/*
 * Copyright 2003-2007 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

package com.argot.remote;

import java.io.IOException;

import com.argot.TypeBound;
import com.argot.TypeConstructor;
import com.argot.TypeConstructorAuto;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeReaderAuto;
import com.argot.TypeWriter;
import com.argot.common.UInt16;
import com.argot.common.U8Ascii;
import com.argot.meta.MetaAbstract;
import com.argot.meta.MetaSequence;

/**
 * This is a remote exception with stack trace.  It allows an exception and full stack
 * trace to be sent back over the wire.  Any local exception will have its
 * full stack trace copied.
 */
public class MetaRemoteException
{
	public static final String TYPENAME = "remote.exception";

	public static class ExceptionConstructor
	implements TypeConstructor
	{
		TypeConstructorAuto _autoConstructor;
		
		public ExceptionConstructor(Class clazz)
		throws TypeException
		{
			_autoConstructor = new TypeConstructorAuto(clazz);
		}
		
		public Object construct(TypeElement sequence, Object[] parameters) 
		throws TypeException 
		{
			// the elements are wrapped in a sequence array.
			Object[] stackTrace = (Object[]) parameters[0];
			
			Object[] exceptionParams = new Object[2];
			exceptionParams[0] = stackTrace[0];
			exceptionParams[1] = stackTrace[1];
			
			Throwable ex = (Throwable) _autoConstructor.construct(sequence, exceptionParams);
			ex.setStackTrace(convertStackTrace((Object[]) stackTrace[2]));
			return ex;
		}
		
		private StackTraceElement[] convertStackTrace( Object[] remoteStack )
		{
			StackTraceElement[] localTrace = new StackTraceElement[ remoteStack.length ];
			for ( int x = 0 ; x < remoteStack.length; x++ )
			{
				MetaRemoteStackTraceElement element = (MetaRemoteStackTraceElement) remoteStack[x];
				localTrace[x] = new StackTraceElement( element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber());
			}
			return localTrace;
		}
		
	}
	
	public static class Reader
	implements TypeLibraryReader,TypeBound
	{
		private TypeReaderAuto _autoReader;
		
		public Reader(Class clazz) 
		throws TypeException
		{
			if (clazz.isAssignableFrom(Exception.class))
			{
				throw new TypeException("RemoteExceptionBasicReader class must extend Exception");
			}
			_autoReader = new TypeReaderAuto(new ExceptionConstructor(clazz));
		}

		public void bind(TypeLibrary library, TypeElement definition, String typeName, int typeId) 
		throws TypeException 
		{
			_autoReader.bind(library, definition, typeName, typeId);
		}
		
		public TypeReader getReader(TypeMap map) 
		throws TypeException 
		{
			return _autoReader.getReader(map);
		}
	}

	public static boolean isWrapRequired(TypeLibrary library, Throwable cause)
	{
		try {
			int id = library.getId(cause.getClass());
			MetaAbstract metaAbstract = (MetaAbstract) library.getStructure( library.getId("remote.exception"));
			return !metaAbstract.isMapped(id);
		} catch (TypeException e) {
			return false;
		}
	}
	
	public static class Writer
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException
		{
			Throwable e = (Throwable) o;
			
			// cut the message off at 255 characters.
			String message = e.getMessage();
			message = (message==null)?"":message.substring(0, (message.length()>255)?255:message.length());
			out.writeObject( U8Ascii.TYPENAME, message );
			
			// if the cause is null simply write out the empty exception type.
			// if the cause is not null check if the cause is defined in the
			// type library.  If it is defined and is mapped to remote.exception
			// it can be written using remote.exception.  If its any other situation
			// then we would like to have the details, so wrap it in
			// a MetaRemoteException and send the details.
			Throwable cause = e.getCause();
			if (cause!=null)
			{
				try {
					TypeLibrary library = out.getTypeMap().getLibrary();
					int id = library.getId(cause.getClass());
					
					MetaAbstract metaAbstract = (MetaAbstract) library.getStructure( library.getId("remote.exception"));
					if (metaAbstract.isMapped(id))
					{
						out.writeObject( "remote.exception", cause);
						return;
					}
				} catch (TypeException ex) {
					// exception not in type library.
				}
				WrappedRemoteException wrapped = new WrappedRemoteException(cause);
				out.writeObject("remote.exception", wrapped);
			} else {
				out.writeObject( "remote.exception#empty", null);
			}
			
			// write out the stack trace array.
			StackTraceElement[] elements = e.getStackTrace();
			out.writeObject( UInt16.TYPENAME, new Integer( elements.length ));			
			for ( int x = 0 ; x < elements.length ; x++ )
			{
				MetaRemoteStackTraceElement trace = new MetaRemoteStackTraceElement( elements[x].getClassName(), elements[x].getMethodName(), elements[x].getFileName(), elements[x].getLineNumber());
				out.writeObject( MetaRemoteStackTraceElement.TYPENAME, trace );
			}
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
	}
}
