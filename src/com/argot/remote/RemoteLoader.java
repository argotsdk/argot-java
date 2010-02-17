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
