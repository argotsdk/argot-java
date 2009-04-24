/*
 * Copyright 2003-2009 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

import com.argot.TypeException;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeWriter;
import com.argot.common.Int32;
import com.argot.common.U8Ascii;

public class MetaRemoteStackTraceElement
{
	public static final String TYPENAME = "remote.stack_trace_element";
	public static final String VERSION = "1.3";
	
	private String _class;
	private String _method;
	private String _file;
	private int _line;
	
	public MetaRemoteStackTraceElement( String clss, String method, String file, int line )
	{
		_class = clss;
		_method = method;
		_file = file;
		_line = (int) line;
	}
	
	public String getClassName()
	{
		return _class;
	}
	
	public String getMethodName()
	{
		return _method;
	}
	
	public int getLineNumber()
	{
		return _line;
	}
	
	public String getFileName()
	{
		return _file;
	}
	
	public String toString()
	{
		return _class + "." + _method + "(" + _file + ":" + _line + ")";
	}

	public static class MetaRemoteStackTraceElementWriter
	implements TypeLibraryWriter,TypeWriter
	{
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException
		{
			MetaRemoteStackTraceElement e = (MetaRemoteStackTraceElement) o;
			out.writeObject( U8Ascii.TYPENAME, e.getClassName() );
			out.writeObject( U8Ascii.TYPENAME, e.getMethodName() );
			out.writeObject( U8Ascii.TYPENAME, e.getFileName() );
			out.writeObject( Int32.TYPENAME, new Integer( e.getLineNumber() ));
		}
		
		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}		
	}
}
