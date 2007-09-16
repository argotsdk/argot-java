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

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeReaderAuto;
import com.argot.TypeReaderInvalid;
import com.argot.TypeWriterInvalid;
import com.argot.meta.MetaMarshaller;

public class RemoteLoader
extends ResourceDictionaryLoader
{
	public static final String DICTIONARY = "remote.dictionary";
	
	public RemoteLoader()
	{
		super( DICTIONARY );
	}
	
	public String getName()
	{
		return DICTIONARY;
	}
	
	public void bind( TypeLibrary library ) 
	throws TypeException
	{
		if ( library.getTypeState( MetaParameter.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaParameter.TYPENAME, new MetaParameter.MetaParameterReader(), new MetaParameter.MetaParameterWriter(), MetaParameter.class );
		}

		if ( library.getTypeState( MetaMethod.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaMethod.TYPENAME, new MetaMethod.MetaMethodReader(), new MetaMethod.MetaMethodWriter(), MetaMethod.class );
		}

		if ( library.getTypeState( MetaInterface.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{		
			library.bind( MetaInterface.TYPENAME, new MetaInterface.MetaInterfaceReader(), new MetaInterface.MetaInterfaceWriter(),MetaInterface.class );
		}

		if ( library.getTypeState( MetaObject.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaObject.TYPENAME, new MetaObject.MetaObjectReader(), new MetaObject.MetaObjectWriter(), MetaObject.class );
		}
		
		if ( library.getTypeState( MetaLocation.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaLocation.TYPENAME,new MetaMarshaller(),new MetaMarshaller(), MetaLocation.class );
		}

		library.bind( "remote.exception", new MetaMarshaller(), new MetaMarshaller(), null );
		library.bind( "remote.exception#empty", new MetaMarshaller(),new MetaMarshaller(), null );
		library.bind( "remote.exception.basic", new MetaMarshaller(),new MetaMarshaller(), null );
		library.bind( "remote.stack_trace_element", new TypeReaderAuto( MetaRemoteStackTraceElement.class ),new MetaRemoteStackTraceElement("","","",0), MetaRemoteStackTraceElement.class );
		library.bind( "remote.exception.wrapped", new MetaRemoteException.Reader(WrappedRemoteException.class), new MetaRemoteException.Writer(), WrappedRemoteException.class );
		library.bind( "remote.exception#wrapped", new MetaMarshaller(),new MetaMarshaller(), null );
	}


}
