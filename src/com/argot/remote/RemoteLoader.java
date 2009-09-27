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

import com.argot.ResourceDictionaryLoader;
import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.auto.TypeReaderAuto;
import com.argot.auto.TypeSimpleReader;
import com.argot.auto.TypeSimpleWriter;
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
		int typeId = library.getTypeId( MetaParameter.TYPENAME, MetaParameter.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeSimpleReader( new MetaParameter.MetaParameterReader()), new TypeSimpleWriter( new MetaParameter.MetaParameterWriter()), MetaParameter.class );
		}

		typeId = library.getTypeId( MetaMethod.TYPENAME, MetaMethod.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeSimpleReader(new MetaMethod.MetaMethodReader()), new TypeSimpleWriter(new MetaMethod.MetaMethodWriter()), MetaMethod.class );
		}

		typeId = library.getTypeId( MetaInterface.TYPENAME, MetaInterface.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{		
			library.bind( typeId, new TypeSimpleReader(new MetaInterface.MetaInterfaceReader()), new TypeSimpleWriter(new MetaInterface.MetaInterfaceWriter()),MetaInterface.class );
		}

		typeId = library.getTypeId( MetaParameter.TYPENAME, MetaParameter.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeSimpleReader(new MetaObject.MetaObjectReader()), new TypeSimpleWriter(new MetaObject.MetaObjectWriter()), MetaObject.class );
		}
		
		typeId = library.getTypeId( MetaObject.TYPENAME, MetaObject.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId, new TypeSimpleReader(new MetaObject.MetaObjectReader()), new TypeSimpleWriter(new MetaObject.MetaObjectWriter()), MetaObject.class );
		}
		
		typeId = library.getTypeId( MetaLocation.TYPENAME, MetaLocation.VERSION );
		if ( library.getTypeState( typeId ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( typeId,new MetaMarshaller(),new MetaMarshaller(), MetaLocation.class );
		}
		

		library.bind( library.getTypeId("remote.exception","1.3"), new MetaMarshaller(), new MetaMarshaller(), null );
		library.bind( library.getTypeId("remote.exception_basic", "1.3"), new MetaMarshaller(),new MetaMarshaller(), null );
		library.bind( library.getTypeId("remote.stack_trace_element", "1.3"), new TypeReaderAuto( MetaRemoteStackTraceElement.class ),new MetaRemoteStackTraceElement.MetaRemoteStackTraceElementWriter(), MetaRemoteStackTraceElement.class );
		library.bind( library.getTypeId("remote.exception_wrapped", "1.3"), new MetaRemoteException.Reader(WrappedRemoteException.class), new MetaRemoteException.Writer(), WrappedRemoteException.class );
	}


}
