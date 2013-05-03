package com.argot.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import junit.framework.TestCase;

import com.argot.TypeException;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryLoader;
import com.argot.common.CommonLoader;
import com.argot.data.MixedData;
import com.argot.data.MixedDataAnnotated;
import com.argot.dictionary.DictionaryLoader;
import com.argot.message.MessageReader;
import com.argot.message.MessageWriter;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryName;
import com.argot.meta.MetaExpression;
import com.argot.meta.MetaExtensionLoader;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaLoader;
import com.argot.meta.MetaName;
import com.argot.meta.MetaReference;
import com.argot.meta.MetaSequence;
import com.argot.meta.MetaTag;
import com.argot.meta.MetaVersion;

public class ModelReaderAutoTest
extends TestCase
{
	private TypeLibrary _library;
	private int _mixedDataTypeId;
	
	TypeLibraryLoader libraryLoaders[] = {
		new MetaLoader(),
		new DictionaryLoader(),
		new MetaExtensionLoader(),
		new CommonLoader()
	};
	
    protected void setUp() throws Exception
    {
        super.setUp();
        _library = new TypeLibrary( libraryLoaders );
        _mixedDataTypeId = register( _library );
    }
    
    
    public void testReadWriteModel() throws Exception
    {
        MixedDataAnnotated data = new MixedDataAnnotated( 2345, (short)234, "Testing");

        // write out complex object to message.
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MessageWriter writer = new MessageWriter( _library );
        writer.writeMessage( baos, _mixedDataTypeId, data );
        baos.close();
        
        
        ByteArrayInputStream bais = new ByteArrayInputStream( baos.toByteArray() );
        Object o = MessageReader.readMessage( _library, bais );
        assertEquals( o.getClass(), data.getClass() );
        MixedDataAnnotated readData = (MixedDataAnnotated) data;
        assertEquals( readData.getInt(), data.getInt() );
        assertEquals( readData.getShort(), data.getShort() );
        assertEquals( readData.getString(), data.getString() );
        
     }   
    
	/*
	 * This should be contained in a dictionary file instead
	 * of being created in code.  Useful here for testing.
	 */
	public static int register( TypeLibrary library )
	throws TypeException
	{
		int id = library.register( new DictionaryName(MetaName.parseName(library,MixedData.TYPENAME)), new MetaIdentity() );
		
		return library.register( 
				new DictionaryDefinition(id, MetaName.parseName(library,MixedData.TYPENAME), MetaVersion.parseVersion("1.0")),
				new MetaSequence(
					new MetaExpression[]{
					    new MetaTag( "uint16", new MetaReference( library.getTypeId("uint16"))),
					    new MetaTag( "uint8", new MetaReference( library.getTypeId("uint8"))),
					    new MetaTag( "u8ascii", new MetaReference( library.getTypeId("u8ascii")))
					}
				),
			new ModelReaderAuto(  ),
			new ModelWriterAuto( ),
			MixedData.class
		);
	}   
}
