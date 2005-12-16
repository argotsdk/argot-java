/*
 * Copyright 2003-2005 (c) Live Media Pty Ltd. <argot@einet.com.au> 
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

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.meta.MetaMarshaller;

public class RemoteTypes 
{

	public static void bindTypes( TypeLibrary library ) 
	throws TypeException
	{
		if ( library.getTypeState( MetaParameter.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			MetaParameter mp = new MetaParameter(0, "" );
			library.bind( MetaParameter.TYPENAME,mp,mp,mp.getClass() );
		}

		if ( library.getTypeState( MetaMethod.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			MetaMethod mm = new MetaMethod( 0, null, (MetaParameter[]) null, (MetaParameter[]) null, (Integer[]) null);
			library.bind( MetaMethod.TYPENAME,mm,mm,mm.getClass() );
		}

		if ( library.getTypeState( MetaInterface.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			MetaInterface mi = new MetaInterface();
			library.bind( MetaInterface.TYPENAME,mi,mi,mi.getClass() );
		}

		if ( library.getTypeState( MetaObject.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			MetaObject mo = new MetaObject( null,0 );
			library.bind( MetaObject.TYPENAME,mo,mo,mo.getClass() );
		}
		
		if ( library.getTypeState( MetaLocation.TYPENAME ) == TypeLibrary.TYPE_REGISTERED )
		{
			library.bind( MetaLocation.TYPENAME,new MetaMarshaller(),new MetaMarshaller(), null );
		}			
	}
}
