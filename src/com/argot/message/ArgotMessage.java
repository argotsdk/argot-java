/*
 * Copyright (c) 2013, Live Media Pty. Ltd.
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

package com.argot.message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;

import com.argot.TypeException;
import com.argot.TypeHelper;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLocation;
import com.argot.TypeMap;
import com.argot.TypeMapper;
import com.argot.TypeMapperCore;
import com.argot.TypeMapperDynamic;
import com.argot.TypeMapperError;
import com.argot.TypeOutputStream;
import com.argot.common.Empty;
import com.argot.common.IEEEDouble;
import com.argot.common.IEEEFloat;
import com.argot.common.Int16;
import com.argot.common.Int32;
import com.argot.common.Int64;
import com.argot.common.Int8;
import com.argot.common.U8Boolean;
import com.argot.common.UInt16;
import com.argot.common.UInt32;
import com.argot.common.UInt64;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;
import com.argot.meta.DictionaryDefinition;
import com.argot.meta.DictionaryLocation;
import com.argot.meta.DictionaryName;
import com.argot.meta.DictionaryRelation;
import com.argot.meta.MetaDefinition;

public class ArgotMessage
{
    private final TypeLibrary _typeLibrary;

	public ArgotMessage( final TypeLibrary library )
	throws TypeException
	{
	    _typeLibrary = library;
	}

	public void writeMessage( final OutputStream outStream, final String typeName, final Object o)
	throws TypeException, IOException
	{
	    final TypeMap messageTypeMap =  new TypeMap( _typeLibrary, new TypeMapperDynamic(new ArgotMessageMapper( new TypeMapperError() )));
	    messageTypeMap.setReference(TypeMap.REFERENCE_MAP, messageTypeMap);

	    // Write the message to a buffer.
	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
		final TypeOutputStream out = new TypeOutputStream(baos, messageTypeMap );
		out.writeObject(typeName, o);
		baos.flush();
		baos.close();

		final TypeOutputStream msgStream = new TypeOutputStream(outStream, messageTypeMap );
		msgStream.writeObject(UInt8.TYPENAME, 0x41 );
		msgStream.writeObject(UInt8.TYPENAME, 0x13 );

		final byte[] dictionaryBuffer = writeMessageDictionary(messageTypeMap);
		msgStream.getStream().write( dictionaryBuffer );

		msgStream.writeObject( UVInt28.TYPENAME, new Integer( messageTypeMap.getStreamId(typeName)));
		msgStream.getStream().write( baos.toByteArray() );
		msgStream.getStream().flush();

	}

	private byte[] writeMessageDictionary(final TypeMap messageTypeMap)
	throws TypeException, IOException
	{

	    final List<Integer> streamIdList = messageTypeMap.getIdList();
	    final List<Integer> baseMessageIdList = ArgotMessageMapper.getMessageIdentifiers();

	    int extensionCount = 0;
        Iterator<Integer> messageIdIterator = messageTypeMap.getIdList().iterator();
        while (messageIdIterator.hasNext() )
        {
            final Integer id = messageIdIterator.next();
            if ( baseMessageIdList.contains(id))
            {
                // already written this out in the core.
                continue;
            }
            extensionCount++;
        }

	    final ByteArrayOutputStream baos = new ByteArrayOutputStream();
	    final TypeOutputStream messageDictionary = new TypeOutputStream(baos, messageTypeMap );

	    System.out.println("writing ext count " + extensionCount );
        messageDictionary.writeObject(UVInt28.TYPENAME, new Integer( extensionCount ));

	    messageIdIterator = streamIdList.iterator();
	    while (messageIdIterator.hasNext())
	    {
	        final Integer id = messageIdIterator.next();

	        // If the identifier is already in the base no need to write it out.
	        if (baseMessageIdList.contains(id)) {
	            continue;
	        }

            final TypeLocation location = messageTypeMap.getLocation(id.intValue());
            final MetaDefinition definition = (MetaDefinition) messageTypeMap.getStructure(id.intValue());

            System.out.println( "writing " + id.intValue() + " " + location.toString());
            messageDictionary.writeObject( UVInt28.TYPENAME, new Integer(id.intValue()));
            messageDictionary.writeObject( DictionaryLocation.TYPENAME, location);
            messageDictionary.writeObject( MetaDefinition.META_DEFINITION_ENVELOPE, definition );

	    }

	    messageDictionary.getStream().close();
	    return baos.toByteArray();
	}

	public Object readMessage( final InputStream in )
	throws TypeException, IOException
	{

	    final TypeMap messageTypeMap =  new TypeMap( _typeLibrary, new ArgotMessageMapper( new TypeMapperError() ));
	    messageTypeMap.setReference(TypeMap.REFERENCE_MAP, messageTypeMap);

	    final TypeInputStream msgStream = new TypeInputStream( in, messageTypeMap );

	    final Short magic = (Short) msgStream.readObject( UInt8.TYPENAME );
	    System.out.println("magic = " + magic );
	    if (magic.intValue() != 65) {
	        throw new TypeException("Message stream not an Argot message");
	    }

	    final Short version = (Short) msgStream.readObject(UInt8.TYPENAME);
	    System.out.println("version = " + version );
	    if (version.intValue() != 19) {
	        throw new TypeException("Invalid version for Argot message");
	    }

	    final Integer dictionaryCount = (Integer) msgStream.readObject(UVInt28.TYPENAME);
	    System.out.println("dictionaryCount = " + dictionaryCount);
	    // Read in all the types and check if the library has a type at the same location.
	    final Triple[] dictionaryTypes = new Triple[dictionaryCount];
	    for (int x = 0 ; x < dictionaryCount; x++ ) {

	        dictionaryTypes[x] = new Triple();
	        dictionaryTypes[x].id = ((Integer)msgStream.readObject( UVInt28.TYPENAME )).intValue();
	        dictionaryTypes[x].location = (TypeLocation) msgStream.readObject(DictionaryLocation.TYPENAME);
	        dictionaryTypes[x].structure = (byte[]) msgStream.readObject( MetaDefinition.META_DEFINITION_ENVELOPE );

	        System.out.println("reading id " + dictionaryTypes[x].id + " " + dictionaryTypes[x].location.getClass().getName());
	       // final int libraryId = _typeLibrary.getTypeId(dictionaryTypes[x].location);
	        final int libraryId = getDictionaryLocation(dictionaryTypes[x].location, messageTypeMap);
	        if (libraryId == -1) {

	        }
	        messageTypeMap.map(dictionaryTypes[x].id, libraryId);
	    }

	    // Check that the structures are the same.
	    for (int x = 0 ; x < dictionaryCount; x++ ) {
	        TypeHelper.isSame(messageTypeMap.getDefinitionId( dictionaryTypes[x].id ), dictionaryTypes[x].location, dictionaryTypes[x].structure, messageTypeMap );
	    }

	    final Integer id = (Integer) msgStream.readObject(UVInt28.TYPENAME);

		return msgStream.readObject(id);
	}

	private int getDictionaryLocation( final TypeLocation location, final TypeMap map ) throws TypeException {
	    if (location instanceof DictionaryName) {
	        // A Name location can be checked directly with local type library.
	        return _typeLibrary.getTypeId(location);
	    }
	    else if (location instanceof DictionaryDefinition) {
	        // A definition doesn't have correct internal nameid and must be fixed prior to checking.
	        final DictionaryDefinition definition = (DictionaryDefinition) location;
	        final int nameId = _typeLibrary.getTypeId(definition.getName());
	        definition.setId(nameId);
	        return _typeLibrary.getTypeId(definition);
	    }
	    else if (location instanceof DictionaryRelation) {

	    }
	    return -1;
	}

	public static class Triple
    {
        public int id;
        public byte[] structure;
        public TypeLocation location;
    }

	   public static class ArgotMessageMapper
	    extends TypeMapperCore
	    {

	        public ArgotMessageMapper(final TypeMapper chain)
	        {
	            super(chain);
	        }

	        @Override
	        public void initialise(final TypeMap map)
	        throws TypeException
	        {
	            super.initialise(map);

	            final TypeLibrary library = map.getLibrary();

	            // Map Definitions
	            map.map( EMPTY_ID, library.getDefinitionId(Empty.TYPENAME, Empty.VERSION) );
	            // Uint8 already mapped in core.
	            map.map( UINT16_ID, library.getDefinitionId(UInt16.TYPENAME, UInt16.VERSION) );
	            map.map( UINT32_ID, library.getDefinitionId(UInt32.TYPENAME, UInt32.VERSION) );
	            map.map( UINT64_ID, library.getDefinitionId(UInt64.TYPENAME, UInt64.VERSION) );
	            map.map( INT8_ID, library.getDefinitionId(Int8.TYPENAME, Int8.VERSION) );
	            map.map( INT16_ID, library.getDefinitionId(Int16.TYPENAME, Int16.VERSION) );
	            map.map( INT32_ID, library.getDefinitionId(Int32.TYPENAME, Int32.VERSION) );
	            map.map( INT64_ID, library.getDefinitionId(Int64.TYPENAME, Int64.VERSION) );
	            map.map( FLOAT32_ID, library.getDefinitionId(IEEEFloat.TYPENAME, IEEEFloat.VERSION) );
	            map.map( DOUBLE64_ID, library.getDefinitionId(IEEEDouble.TYPENAME, IEEEDouble.VERSION) );
	            map.map( U8BOOLEAN_ID, library.getDefinitionId(U8Boolean.TYPENAME, U8Boolean.VERSION) );
	        }

	        public static List<Integer> getMessageIdentifiers() {
	            final List<Integer> ids = getCoreIdentifiers();

	            ids.add(EMPTY_ID);
	            ids.add(UINT16_ID);
	            ids.add(UINT32_ID);
	            ids.add(UINT64_ID);
	            ids.add(INT8_ID);
	            ids.add(INT16_ID);
	            ids.add(INT32_ID);
	            ids.add(INT64_ID);
	            ids.add(FLOAT32_ID);
	            ids.add(DOUBLE64_ID);
	            ids.add(U8BOOLEAN_ID);

	            return ids;
	        }

	        // Common Data Types.
	        public static int EMPTY_ID = 39;
	        public static int UINT16_ID = 40;
	        public static int UINT32_ID = 41;
	        public static int UINT64_ID = 42;
	        public static int INT8_ID = 43;
	        public static int INT16_ID = 44;
	        public static int INT32_ID = 45;
	        public static int INT64_ID = 46;
	        public static int FLOAT32_ID = 47;
	        public static int DOUBLE64_ID = 48;
	        public static int U8BOOLEAN_ID = 49;
	    }
}
