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
package com.argot;

import java.util.Set;

import junit.framework.TestCase;

import com.argot.meta.DictionaryBase;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaCluster;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;

public class TypeLibraryTest
extends TestCase
{
    private TypeLibrary _library;
    
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary();
        _library.register(new DictionaryBase(), new MetaCluster() );
    }
    
    public void testCreateTypeLibrary() throws Exception
    {
        TypeLibrary library = new TypeLibrary();
        assertNotNull( library );
    }
    
    /**
     * Test reserve of a type.
     * @throws Exception
     */
  /*
    public void testReserveType() throws Exception
    {
       // _library.reserve("test","1.0");
    	_library.register( new DictionaryName("test"), new MetaIdentity());
        
        //assertEquals( _library.getTypeState("test","1.0"), TypeLibrary.TYPE_RESERVED );
    	
  
    	assertEquals( _library.getTypeState(_library.getTypeId("test")), TypeLibrary.TYPE_RESERVED );
    }
 */
    public void testReserveAlreadyDefinedType() throws Exception
    {
    	//_library.reserve("test","1.0");
    	_library.register( new DictionaryName(_library,"test"), new MetaIdentity());
    	
    	try
		{
			//_library.reserve("test","1.0");
        	_library.register( new DictionaryName(_library,"test"), new MetaIdentity());
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore.
		}
    }
    
    public void testNotDefinedTypeState() throws Exception
    {
        //int id = _library.getTypeState("blah","1.3");
    	int id = _library.getTypeState(2);
        assertEquals( id, TypeLibrary.TYPE_NOT_DEFINED );
    }
    
    public void testRegisterStructure() throws Exception
    {
        //_library.register( "test","1.0", new TestTypeElement() );
    	TestTypeElement element = new TestTypeElement();
    	_library.register( new DictionaryName(_library,"test"), element );
        //assertEquals( _library.getTypeState("test","1.0"), TypeLibrary.TYPE_REGISTERED );
    	assertEquals( _library.getTypeState(_library.getTypeId("test")), TypeLibrary.TYPE_REGISTERED );
    }
    
    public void testRegisterComplete() throws Exception
    {
        //_library.register( "test","1.0", new TestTypeElement(), new TestReader(), new TestWriter(), null );
        //assertEquals( _library.getTypeState("test","1.0"), TypeLibrary.TYPE_COMPLETE );
        _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), new TestWriter(), null );
        assertEquals( _library.getTypeState( _library.getTypeId("test") ), TypeLibrary.TYPE_COMPLETE );
    }
    
    public void testRegisterInvalidName() throws Exception
    {
        try
		{
			//_library.register( "","", new TestTypeElement(), new TestReader(), new TestWriter(), null );
			_library.register( null,new TestTypeElement(), new TestReader(), new TestWriter(), null );
			fail();
		}
		catch (TypeException e)
		{
			// ignore
		}
		
        try
		{
			//_library.register( "","", new TestTypeElement(), new TestReader(), new TestWriter(), null );
			_library.register( new DictionaryName(_library,""),new TestTypeElement(), new TestReader(), new TestWriter(), null );
			fail();
		}
		catch (TypeException e)
		{
			// ignore
		}
		
    }
    
    public void testRegisterInvalidTypeElement() throws Exception
    {
        try
		{
			//_library.register( "test","1.0", null, new TestReader(), new TestWriter(), null );
			_library.register( new DictionaryName(_library,"test"), null, new TestReader(), new TestWriter(), null );
			fail();
		}
		catch (TypeException e)
		{
			// ignore
		}
    }
    
    public void testRegisterInvalidReader() throws Exception
    {
        try
		{
			//_library.register( "test","1.0", new TestTypeElement(), null, new TestWriter(), null );
			_library.register( new DictionaryName(_library,"test"), new TestTypeElement(), null, new TestWriter(), null );
			fail();
		}
		catch (TypeException e)
		{
			// ignore
		}
    }
    
    public void testRegisterInvalidWriter() throws Exception
    {
        try
		{
			//_library.register( "test","1.0", new TestTypeElement(), new TestReader(), null, null );
        	_library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), null, null );
			fail();
		}
		catch (TypeException e)
		{
			// ignore
		}
    }    
    
    public void testRegisterAfterReserve() throws Exception
    {
        //_library.reserve( "test","1.0" );
        //_library.register( "test","1.0", new TestTypeElement(), new TestReader(), new TestWriter(), null );
        //assertEquals( _library.getTypeState("test","1.0"), TypeLibrary.TYPE_COMPLETE );
    	int id = _library.register( new DictionaryName(_library,"test"), new MetaIdentity());
        _library.register( new DictionaryDefinition(_library,id,"test", "1.3"), new TestTypeElement(), new TestReader(), new TestWriter(), null );
        assertEquals( _library.getTypeState( _library.getDefinitionId("test","1.3")), TypeLibrary.TYPE_COMPLETE );
        	
        TypeElement structure = _library.getStructure( _library.getDefinitionId("test","1.3"));
        assertNotNull( structure );
    }
    
    public void testBind() throws Exception
    {
        //_library.register( "test","1.0", new TestTypeElement() );
        //_library.bind( "test",null,"1.0" );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement() );
        _library.bind( id, new TestReader(), new TestWriter(), null );
    }
    
    public void testBindException() throws Exception
    {
        try
        {
            _library.bind( 10, new TestReader(), new TestWriter(), null );
            fail("expected exception");
        }
        catch( TypeException ex )
        {
            // ignore.
        }
    }

    public void testGetTypeStateNull() throws Exception
    {
    	int state = _library.getTypeState( 10 );
    	assertEquals( state, TypeLibrary.TYPE_NOT_DEFINED );
    }
    
    public void testGetTypeStateReserved() throws Exception
    {
    	//_library.reserve("test","1.0");
    	
    	//int state = _library.getTypeState("test","1.0");
    	//assertEquals( state, TypeLibrary.TYPE_RESERVED );
    }
    
    public void testGetTypeStateNotDefined() throws Exception
    {
    	//int state = _library.getTypeState("test","1.0");
       	int state = _library.getTypeState( 10 );
        assertEquals( state, TypeLibrary.TYPE_NOT_DEFINED );
    }

    public void testGetTypeStateRegistered() throws Exception
    {
        //int id = _library.register( "test","1.0", new TestTypeElement() );
        int id = _library.register( new DictionaryName(_library,"test"),new TestTypeElement() );
    	
        //int state = _library.getTypeState("test","1.0");
        int state = _library.getTypeState( id );
    	assertEquals( state, TypeLibrary.TYPE_REGISTERED );    	
    }

    public void testGetTypeStateComplete() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"),new TestTypeElement(), new TestReader(), writer, writer.getClass() );

    	int state = _library.getTypeState( id );
    	assertEquals( state, TypeLibrary.TYPE_COMPLETE );    	
    }
    
    public void testGetStructure() throws Exception
    {
        TestTypeElement element = new TestTypeElement();
        //_library.register( "test","1.0", element, new TestReader(), new TestWriter(), null );
        int id = _library.register( new DictionaryName(_library,"test"), element, new TestReader(), new TestWriter(), null );
        
        TypeElement elem = _library.getStructure( id );
        assertEquals( element, elem );
    }
    
    public void testGetStructureBadState() throws Exception
    {
    	//_library.reserve( "test","1.0");
    	
    	
    	try
		{
			//_library.getStructure( _library.getDefinitionId("test","1.0"));
    		_library.getStructure( 10 );
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}
    }
    
    public void testGetStructureUndefined() throws Exception
    {
        try
		{
			_library.getStructure( 10 );
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}
    	
    }
     
    public void testGetReader() throws Exception
    {
        TestReader reader = new TestReader();
        //_library.register( "test","1.0", new TestTypeElement(), reader, new TestWriter(), null );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), reader, new TestWriter(), null );
        
        //TypeLibraryReader read = _library.getReader( _library.getDefinitionId("test","1.0"));
        TypeLibraryReader read = _library.getReader( id );
        assertNotNull( read );
    }
    
    public void testGetReaderFail() throws Exception
    {
        //_library.register( "test","1.0", new TestTypeElement() );
    	int id = _library.register( new DictionaryName(_library,"test"),new TestTypeElement() );
        
        try
        {
            //TypeLibraryReader read = _library.getReader( _library.getDefinitionId("test","1.0"));
        	TypeLibraryReader read = _library.getReader( id );
            assertNotNull(read);
            fail();
        } 
        catch ( TypeException ex )
        {
            // ignore.
        }
        
        try
        {
            //TypeLibraryReader read = _library.getReader( _library.getDefinitionId("badtype","1.0"));
        	TypeLibraryReader read = _library.getReader( 10 );
            assertNotNull( read );
            fail();
        } 
        catch ( TypeException ex )
        {
            // ignore.
        }
                
    }
    
    public void testGetWriter() throws Exception
    {
        TestWriter writer = new TestWriter();
        //_library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, null );
        int id = _library.register( new DictionaryName(_library,"test"),new TestTypeElement(), new TestReader(), writer, null );
        
        //TypeLibraryWriter write = _library.getWriter( _library.getDefinitionId("test","1.0"));
        TypeLibraryWriter write = _library.getWriter( id );
        assertNotNull( write );
    }
    
    public void testGetWriterFail() throws Exception
    {
        //_library.register( "test","1.0", new TestTypeElement() );
    	int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement() );
        
        try
        {
            //TypeLibraryWriter read = _library.getWriter( _library.getDefinitionId("test","1.0"));
            TypeLibraryWriter read = _library.getWriter( id );
            assertNotNull(read);
            fail();
        } 
        catch ( TypeException ex )
        {
            // ignore.
        }
        
        try
        {
            //TypeLibraryWriter read = _library.getWriter( _library.getDefinitionId("badtype","1.0"));
        	TypeLibraryWriter read = _library.getWriter( 10 );
            assertNotNull(read);
            fail();
        } 
        catch ( TypeException ex )
        {
            // ignore.
        }
                
    }
    
    public void testGetClass() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"),new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        
        Class<?> clss = _library.getClass( id );
        assertEquals( clss, writer.getClass() );      
    }

    public void testGetClassInvalidId() throws Exception
    {
        try
		{
			_library.getClass( 10 );
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}      
    }    

    public void testGetClassNoClass() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, null );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, null );

        try
		{
			_library.getClass( id );
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}      
    }    
    
    public void testGetClassId() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        
        int[] cids = _library.getId( writer.getClass() );
        assertEquals( 1, cids.length );
        assertEquals( id, cids[0] );      
    }
    
    public void testGetClassIdAfterReserve() throws Exception
    {
        TestWriter writer = new TestWriter();
        //_library.reserve("test","1.0");
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        
        int[] cids = _library.getId( writer.getClass() );
        assertEquals( 1, cids.length );
        assertEquals( id, cids[0] );      
    }
    
    public void testGetName() throws Exception
    {
        TestWriter writer = new TestWriter();
       // _library.reserve("test","1.0");
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );

        MetaName name = _library.getName(id);
        assertEquals(name.getFullName(),"test");
    }

    public void testGetNameInvalid() throws Exception
    {
        try
		{
			_library.getName(40);
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}
        
    }
    
    
    public void testRegisterAfterReserveCheckId() throws Exception
    {
        //int idres = _library.reserve("test","1.0");
        //int idreg = _library.register( "test","1.0", new TestTypeElement() );

        //assertEquals( idres, idreg );
    }
    
    public void testMixedCaseNames() throws Exception
    {
        //int id1 = _library.reserve( "TeSt" ,"1.0");
    	int id1 = _library.register( new DictionaryName(_library,"TeSt"), new MetaIdentity());
        assertEquals( "TeSt", _library.getName( id1 ).getFullName());
        
        //int id2 = _library.register( "TeSt2","1.0", new TestTypeElement() );
        int id2 = _library.register( new DictionaryName(_library,"TeSt2"), new MetaIdentity() );
        assertEquals( "TeSt2", _library.getName( id2 ).getFullName());
        
        TestWriter writer = new TestWriter();
        //int id3 = _library.register( "teSt3","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id3 = _library.register( new DictionaryName(_library,"teSt3"), new MetaIdentity(), new TestReader(), writer, writer.getClass() );
        assertEquals( "teSt3", _library.getName( id3 ).getFullName());
        
        assertEquals( id1, _library.getTypeId( "TeSt" ));
    }
    
    public void testNullNameForGetId() throws Exception
    {
        try
        {
            _library.getDefinitionId( (String) null , null);
            fail("expected TypeException");
        } 
        catch (TypeException e)
        {
            // ignore.  correct.
        }
    }
    
    public void testAddClassAlias() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
    	
        _library.addClassAlias(id, Boolean.class);
    }
    
    public void testAddClassAliasOverloaded() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id = _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
    	
        try
		{
			_library.addClassAlias(id, writer.getClass());
			fail("shouldn't get here");
		}
		catch (TypeException e)
		{
			// ignore
		}
    }
    
    public void testAddClassAliasOverloadedOk() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id1 = _library.register( new DictionaryName(_library,"test1"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        int id2 = _library.register( new DictionaryName(_library,"test2"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );
    	
		//	_library.addClassAlias(id, String.class);
		int ids[] = _library.getId(writer.getClass());
		assertTrue(ids.length == 2 );
		for (int x=0; x<ids.length;x++)
		{
			if ( id1 != ids[x] && id2 != ids[x] )
				fail("invalid ids returned");
		}
    }    
    
    public void testGetNames() throws Exception
    {
        TestWriter writer = new TestWriter();
        //int id = _library.register( "test","1.0", new TestTypeElement(), new TestReader(), writer, writer.getClass() );
        _library.register( new DictionaryName(_library,"test"), new TestTypeElement(), new TestReader(), writer, writer.getClass() );

        Set<String> set = _library.getNames();
        assertEquals( set.size(), 1 );
    }
}
