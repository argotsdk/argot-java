/*
 * Copyright (c) 2003-2019, Live Media Pty. Ltd.
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
package com.argot.meta;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.argot.TypeBound;
import com.argot.TypeElement;
import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibrary;
import com.argot.TypeLibraryReader;
import com.argot.TypeLibraryWriter;
import com.argot.TypeLocation;
import com.argot.TypeLocationBase;
import com.argot.TypeLocationDefinition;
import com.argot.TypeLocationName;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeRelation;
import com.argot.TypeWriter;
import com.argot.common.UInt8;
import com.argot.common.UVInt28;

public class MetaAbstract extends MetaExpression implements MetaDefinition, TypeRelation {
    public static final String TYPENAME = "meta.abstract";
    public static final String VERSION = "1.3";

    protected Map<Integer, MetaMap> _concreteToMap;

    private final Map<String, Integer> _relationMap;

    private MetaAbstractMap _defaultMappings[];

    // This keeps a list of any abstract types we've mapped.
    private final List<MetaAbstract> _abstractParentMap;

    protected class MetaMap {
        public boolean isHidden;
        public int mapTypeId;
    }

    public MetaAbstract() {
        _concreteToMap = new HashMap<Integer, MetaMap>();
        //_mapToConcrete = new HashMap();
        _relationMap = new HashMap<String, Integer>();
        _abstractParentMap = new ArrayList<MetaAbstract>();
        _defaultMappings = null;
    }

    public MetaAbstract(final Object mappings[]) {
        this();
        _defaultMappings = new MetaAbstractMap[mappings.length];

        for (int x = 0; x < mappings.length; x++) {
            _defaultMappings[x] = (MetaAbstractMap) mappings[x];
        }
    }

    public MetaAbstract(final MetaAbstractMap defaultMappings[]) {
        this();
        _defaultMappings = defaultMappings;
    }

    @Override
    public void bind(final TypeLibrary library, final int memberTypeId, final TypeLocation location, final TypeElement definition) throws TypeException {
        super.bind(library, memberTypeId, location, definition);
        if (_defaultMappings != null) {
            for (int x = 0; x < _defaultMappings.length; x++) {
                final MetaAbstractMap mapping = _defaultMappings[x];
                if (mapping == null) {
                    throw new TypeException("MetaAbstract: Default mapping " + x + " invalid");
                }

                final TypeElement concreteElement = library.getStructure(mapping.getConcreteType());
                if (concreteElement instanceof MetaAbstract) {
                    ((MetaAbstract) concreteElement).addAbstractMap(this);
                } else if (!(concreteElement instanceof MetaIdentity)) {
                    throw new TypeException("Can only map abstract types to identity or abstract definitions");
                }
                addMap(mapping.getConcreteType(), TypeLibrary.NOTYPE, true);
            }
        }
    }

    @Override
    public String getTypeName() {
        return TYPENAME;
    }

    public int getConcreteId(final int id) {
        final MetaMap m = _concreteToMap.get(id);
        if (m == null) {
            return -1;
        }
        return m.mapTypeId;
    }

    public Integer[] getConcreteIds() {
        return _concreteToMap.keySet().toArray(new Integer[_concreteToMap.keySet().size()]);
    }

    public void addAbstractMap(final MetaAbstract parent) {
        _abstractParentMap.add(parent);

        final Iterator<Integer> i = _concreteToMap.keySet().iterator();
        while (i.hasNext()) {
            final Integer c = i.next();
            final MetaMap m = _concreteToMap.get(c);

            parent.addMap(c.intValue(), m.mapTypeId, m.isHidden);
        }

    }

    public void addMap(final int concreteType, final int mapType, final boolean isHidden) {
        final MetaMap metaMap = new MetaMap();
        metaMap.isHidden = isHidden;
        metaMap.mapTypeId = mapType;

        _concreteToMap.put(Integer.valueOf(concreteType), metaMap);

        final Iterator<MetaAbstract> i = _abstractParentMap.iterator();
        while (i.hasNext()) {
            final MetaAbstract p = i.next();

            p.addMap(concreteType, mapType, isHidden);
        }
    }

    @Override
    public int getRelation(final String tag) {
        final Integer i = _relationMap.get(tag);
        if (i == null) {
            return TypeLibrary.NOTYPE;
        }
        return i.intValue();
    }

    @Override
    public void setRelation(final String tag, final int id) {
        _relationMap.put(tag, Integer.valueOf(id));
    }

    public static class MetaAbstractTypeReader implements TypeReader, TypeBound, TypeLibraryReader, MetaExpressionReader {
        private final MetaMarshaller _reader;

        public MetaAbstractTypeReader() {
            _reader = new MetaMarshaller();
        }

        @Override
        public void bind(final TypeLibrary library, final int definitionId, final TypeElement definition) throws TypeException {
            _reader.bind(library, definitionId, definition);
        }

        @Override
        public Object read(final TypeInputStream in) throws TypeException, IOException {
            final Object[] object = (Object[]) _reader.getReader(in.getTypeMap()).read(in);
            if (object.length != 1 && !(object[0] instanceof Object[])) {
                throw new TypeException("Unexpected data");
            }
            final Object[] mapArray = (Object[]) object[0];
            final MetaAbstractMap[] defaultMaps = new MetaAbstractMap[mapArray.length];
            for (int x = 0; x < mapArray.length; x++) {
                defaultMaps[x] = (MetaAbstractMap) mapArray[x];
                //defaultMaps[x].setConcreteType(in.getTypeMap().getNameId(defaultMaps[x].getConcreteType()));
            }

            return new MetaAbstract(defaultMaps);
        }

        @Override
        public TypeReader getReader(final TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public TypeReader getExpressionReader(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element) throws TypeException {
            final MetaAbstract metaAbstract = (MetaAbstract) element;
            return new MetaAbstractReader(metaAbstract, map.getReader(map.getStreamId(UVInt28.TYPENAME)));
        }
    }

    public static class MetaAbstractTypeWriter implements TypeWriter, TypeLibraryWriter, MetaExpressionWriter {

        @Override
        public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
            final MetaAbstract ma = (MetaAbstract) o;

            // MetaAbstract uses UVInt28 as part of reading a data type.  This will ensure the type is mapped.
            final TypeMap refMap = (TypeMap) out.getTypeMap().getReference(TypeMap.REFERENCE_MAP);
            refMap.getStreamId(UVInt28.TYPENAME);

            out.writeObject(UInt8.TYPENAME, Integer.valueOf(ma._defaultMappings.length));

            final MetaAbstractMap[] defaultMappings = ma._defaultMappings;
            for (int x = 0; x < defaultMappings.length; x++) {
                out.writeObject(MetaAbstractMap.TYPENAME, defaultMappings[x]);
            }
        }

        @Override
        public TypeWriter getWriter(final TypeMap map) throws TypeException {
            return this;
        }

        @Override
        public TypeWriter getExpressionWriter(final TypeMap map, final MetaExpressionResolver resolver, final TypeElement element) throws TypeException {
            final MetaAbstract metaAbstract = (MetaAbstract) element;
            return new MetaAbstractWriter(metaAbstract, map.getWriter(map.getStreamId(UVInt28.TYPENAME)));
        }
    }

    public static class MetaAbstractReader implements TypeReader {
        private final MetaAbstract _metaAbstract;
        private final TypeReader _uvint28;
        private final Map<Integer, TypeReader> _mapCache;

        public MetaAbstractReader(final MetaAbstract metaAbstract, final TypeReader uvint28) {
            _metaAbstract = metaAbstract;
            _uvint28 = uvint28;
            _mapCache = new HashMap<Integer, TypeReader>();
        }

        @Override
        public Object read(final TypeInputStream in) throws TypeException, IOException {
            final Integer type = (Integer) _uvint28.read(in);
            TypeReader mappedReader = _mapCache.get(type);
            if (mappedReader == null) {
                final int id = mapType(in.getTypeMap(), type);
                mappedReader = in.getTypeMap().getReader(id);
                _mapCache.put(type, mappedReader);
            }
            return mappedReader.read(in);
        }

        private int mapType(final TypeMap map, final Integer type) throws TypeException {
            final int definitionId = map.getDefinitionId(type.intValue());
            final TypeLocation location = map.getLibrary().getLocation(definitionId);
            int identityId = -1;
            if (location instanceof TypeLocationDefinition) {
                final TypeLocationDefinition definition = (TypeLocationDefinition) location;
                identityId = definition.getId();
            } else if (location instanceof TypeLocationName) {
                identityId = definitionId;
            } else if (location instanceof TypeLocationBase) {
                identityId = definitionId;
            } else {
                throw new TypeException("Unknown location type");
            }

            final MetaMap concrete = _metaAbstract._concreteToMap.get(Integer.valueOf(identityId));
            if (concrete == null) {
                throw new TypeException("type not mapped:" + type.intValue() + " " + map.getName(type.intValue()).getFullName());
            }
            return type.intValue();
        }

    }

    private static class CacheEntry {
        Integer mapId;
        TypeWriter idWriter;
    }

    public static class MetaAbstractWriter implements TypeWriter {
        private final MetaAbstract _metaAbstract;
        private final TypeWriter _uvint28;
        private final Map<Class<?>, CacheEntry> _mapCache;

        public MetaAbstractWriter(final MetaAbstract metaAbstract, final TypeWriter uvint28) {
            _metaAbstract = metaAbstract;
            _uvint28 = uvint28;
            _mapCache = new HashMap<Class<?>, CacheEntry>();
        }

        @Override
        public void write(final TypeOutputStream out, final Object o) throws TypeException, IOException {
            final Class<?> clss = o.getClass();
            CacheEntry entry = _mapCache.get(clss);
            if (entry == null) {
                entry = mapType(out.getTypeMap(), clss);
                _mapCache.put(clss, entry);
            }
            _uvint28.write(out, entry.mapId);
            entry.idWriter.write(out, o);
        }

        private CacheEntry mapType(final TypeMap map, final Class<?> clss) throws TypeException {
            // assumes class is bound to definition, not identity.
            final int[] ids = map.getLibrary().getId(clss);
            if (ids.length != 1) {
                if (ids.length > 1) {
                    throw new TypeException("Class bound to multiple system types:" + clss.getName());
                }
                if (ids.length == 0) {
                    throw new TypeException("Class not bound to any mapped type:" + clss.getName());
                }
            }
            final int defId = ids[0];

            final MetaName name = map.getLibrary().getName(defId);

            // get the identity id.
            final int id = map.getLibrary().getTypeId(name);

            // Find the identifier for the abstract map definition using identity internal id.
            final MetaMap metaMap = _metaAbstract._concreteToMap.get(Integer.valueOf(id));
            if (metaMap == null) {
                throw new TypeException(
                                "MetaAbstract - Definition '" + name.getFullName() + "' not mapped to abstract type '" + map.getLibrary().getName(_metaAbstract.getMemberTypeId()).getFullName() + "'");
            }

            final CacheEntry entry = new CacheEntry();

            if (!metaMap.isHidden) {
                // This is very important.  When working as a remote client we must ensure
                // that the server also has the mapping from concrete to map value.  By
                // getting the System Id of the MapId it will automatically resolve if the
                // server has the correct value.
                try {
                    map.getStreamId(metaMap.mapTypeId);
                    map.getStreamId(id);
                } catch (final TypeException ex) {
                    throw new TypeException("MetaAbstract - Definition '" + name.getFullName() + "' not mapped to abstract type '"
                                    + map.getLibrary().getName(_metaAbstract.getMemberTypeId()).getFullName() + "'");
                }
            }

            // The Id written to file is the mapped concrete id.
            try {
                entry.mapId = Integer.valueOf(map.getStreamId(defId));
            } catch (final TypeException ex) {
                final MetaVersion version = map.getLibrary().getVersion(defId);
                throw new TypeException("MetaAbstract - Definition '" + name.getFullName() + "/" + version.toString() + "' not mapped to abstract type '"
                                + map.getLibrary().getName(_metaAbstract.getMemberTypeId()).getFullName() + "'");
            }
            // Cache the writer function.
            entry.idWriter = map.getWriter(map.getStreamId(defId));
            return entry;
        }

    }

    public boolean isMapped(final int id) {
        return _concreteToMap.get(Integer.valueOf(id)) == null ? false : true;
    }

}
