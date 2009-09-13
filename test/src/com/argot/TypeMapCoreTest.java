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
package com.argot;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.argot.dictionary.Dictionary;
import com.argot.meta.MetaFixedWidth;
import com.argot.meta.MetaLoader;

import junit.framework.TestCase;

public class TypeMapCoreTest
extends TestCase
{
    private TypeLibrary _library;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        _library.loadLibrary( new MetaLoader() );
    }
    
    public void testTypeMapCore() throws Exception
    {
		TypeMap baseMap = new TypeMap( _library, new TypeMapperCore(new TypeMapperError()));
		ReferenceTypeMap coreMap = new ReferenceTypeMap( _library, new TypeMapperCore(new TypeMapperError()), baseMap);
  	
        byte[] core = writeCore(coreMap);
        
        int count=0;
        System.out.println("Core Size: " + core.length);
        for (int x=0; x<core.length;x++)
        {
        	count++;
        	String value = Integer.toString( core[x], 16 );
        	if (value.length()==1) value = "0" + value;
        	value = "" + value;
        	System.out.print( "" + value + " ");
        	if (count>30)
        	{
        		count=0;
        		System.out.println("");
        	}
        }
    }
    
    public void testGetClass() throws Exception
    {
       // TypeMap map = TypeMapCore.getCoreTypeMap( _library );
        
      //  int id = map.getStreamId( MetaFixedWidth.class );
      //  assertEquals( id, map.getStreamId( MetaFixedWidth.TYPENAME ));
    }
    
	public static byte[] writeCore( TypeMap map ) throws TypeException, IOException
	{
		TypeMap refCore = new TypeMap(map.getLibrary(), new TypeMapperCore(new TypeMapperError()));
		ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
		TypeOutputStream coreObjectStream = new TypeOutputStream( baos1, map );
		coreObjectStream.writeObject( Dictionary.DICTIONARY_ENTRY_LIST, refCore );
		baos1.close();		
		return baos1.toByteArray();
	}    

}
