/*
 * Copyright (c) 2003-2013, Live Media Pty. Ltd.
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.argot.auto.ArgotMarshaller;
import com.argot.auto.TypeClassInstantiator;
import com.argot.common.CommonLoader;
import com.argot.meta.MetaCluster;
import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaLoader;
import com.argot.meta.MetaName;
import com.argot.meta.MetaVersion;

/**
 * The TypeLibrary is central to Argot. It provides the collection of
 * definitions in any system.
 *
 * A TypeLibrary has a collection of names, collection of definitions (many to
 * one name), and associates classes to definitions (one to one). The interface
 * allows looking up based on names, definition id's or classes.
 *
 * @author David Ryan
 */
public class TypeLibrary
{
	public static int NOTYPE = -1;

	public static int TYPE_NOT_DEFINED = 0;
	public static int TYPE_RESERVED = 1;
	public static int TYPE_REGISTERED = 2;
	public static int TYPE_COMPLETE = 3;

	public static final String[] typeStates = {"Type not defined", "type reserved", "type registered", "type complete"};

	private ArrayList<TypeDefinitionEntry> _types; // index(nameId) to
													// TypeNameEntry

	private HashMap<String, TypeDefinitionEntry> _names; // name to
															// TypeNameEntry

	private HashMap<Class<?>, List<TypeDefinitionEntry>> _classes; // class to
																	// TypeDefinitionEntry

	// private TreeItem _tree;
	private MetaCluster _tree;

	private boolean _primed;

	private class TypeDefinitionEntry
	{
		public int state;
		public int id;
		public TypeLibraryReader reader;
		public TypeLibraryWriter writer;
		public Class<?> clss;
		public TypeLocation location;
		public TypeElement structure;
		public TypeInstantiator instantiator;
		public boolean isSimple;
	}

	public TypeLibrary() throws TypeException
	{
		init();
		loadBaseTypes();
	}

	public TypeLibrary(final boolean loadBaseTypes) throws TypeException
	{
		init();

		if (loadBaseTypes)
		{
			loadBaseTypes();
		}
	}

	public TypeLibrary(final TypeLibraryLoader[] loaders) throws TypeException
	{
		init();

		loadLibraries(loaders);
	}

	private void init()
	{
		// System.out.println("\nArgot Version 1.3.b4");
		// System.out.println("Copyright 2003-2013 (C) Live Media Pty Ltd.");
		// System.out.println("www.argot-sdk.org\n");

		_types = new ArrayList<TypeDefinitionEntry>();
		_names = new HashMap<String, TypeDefinitionEntry>();
		_classes = new HashMap<Class<?>, List<TypeDefinitionEntry>>();
		_tree = null;
		_primed = false;
	}

	private void loadBaseTypes() throws TypeException
	{
		final TypeLibraryLoader libraryLoaders[] = {new MetaLoader(), new CommonLoader()};

		loadLibraries(libraryLoaders);
	}

	public void loadLibraries(final TypeLibraryLoader[] loaders) throws TypeException
	{
		for (int x = 0; x < loaders.length; x++)
		{
			loadLibrary(loaders[x]);
		}
	}

	public void loadLibrary(final TypeLibraryLoader loader) throws TypeException
	{
		loader.load(this);
	}

	public void setPrimed()
	{
		_primed = true;
	}

	private void addClass(final TypeDefinitionEntry definition, final Class<?> clss) throws TypeException
	{
		List<TypeDefinitionEntry> list = _classes.get(clss);
		if (list == null)
		{
			list = new ArrayList<TypeDefinitionEntry>();
			_classes.put(clss, list);
		}
		else
		{
			final Iterator<TypeDefinitionEntry> iter = list.iterator();
			while (iter.hasNext())
			{
				final TypeDefinitionEntry entry = iter.next();

				if (entry == definition)
				{
					throw new TypeException("Class already bound to type");
				}
			}
		}
		list.add(definition);

	}

	/*
	 * Used to add any new names to the tree structure.
	 */
	private void addLocationToTree(final MetaName location, final TypeDefinitionEntry entry) throws TypeException
	{
		final TypeElement data = getStructure(location.getGroup());
		if (data == null)
		{
			throw new TypeException("Structure not found");
		}

		if (!(data instanceof MetaCluster))
		{
			throw new TypeException("Name location not in group");
		}

		final MetaCluster group = (MetaCluster) data;

		group.put(location.getName(), entry.structure);
	}

	/**
	 * Add a new definition to the library. This must be a fresh entry. Either a
	 * reserved or register call.
	 * 
	 * @param definition
	 * @return
	 */
	private int add(final TypeDefinitionEntry definition) throws TypeException
	{
		MetaIdentity identity = null;
		MetaVersion identityVersion = null;
		TypeRelation relation = null;
		String relationTag = null;

		// Add to list of names if it isn't already there.
		if (definition.location instanceof TypeLocationName)
		{
			final MetaName metaName = ((TypeLocationName) definition.location).getName();

			final String name = checkName(metaName.getFullName());

			final TypeDefinitionEntry nameEntry = _names.get(name);
			if (nameEntry != null)
			{
				throw new TypeDefinedException("Type name already exists");
			}

			addLocationToTree(metaName, definition);

			// Add name lookup in names map.
			_names.put(name, definition);
		}
		else if (definition.location instanceof TypeLocationDefinition)
		{
			final TypeLocationDefinition typeDef = (TypeLocationDefinition) definition.location;

			// String typeName = this.getName(tv.getTypeId()) + "#" +
			// checkVersion( tv.getVersion() );
			if (!isTypeIdInRange(typeDef.getId()))
			{
				throw new TypeException("Type not in range");
			}

			final TypeDefinitionEntry nameEntry = _types.get(typeDef.getId());
			if (nameEntry == null)
			{
				throw new TypeNotDefinedException("Type name not defined");
			}

			if (!(nameEntry.structure instanceof MetaIdentity))
			{
				throw new TypeException("Location not a name type");
			}

			identity = (MetaIdentity) nameEntry.structure;
			identityVersion = typeDef.getVersion();
		}
		else if (definition.location instanceof TypeLocationRelation)
		{
			final TypeLocationRelation typeRel = (TypeLocationRelation) definition.location;

			if (!isTypeIdInRange(typeRel.getId()))
			{
				throw new TypeException("Type not in range");
			}

			final TypeDefinitionEntry targetEntry = _types.get(typeRel.getId());
			if (targetEntry == null)
			{
				throw new TypeNotDefinedException("Relation target name not defined");
			}

			if (!(targetEntry.structure instanceof TypeRelation))
			{
				throw new TypeException("TypeLocationRelation target not a TypeRelation");
			}

			relation = (TypeRelation) targetEntry.structure;
			relationTag = typeRel.getTag();
		}
		else if (definition.location instanceof TypeLocationBase)
		{
			if (_tree != null)
			{
				throw new TypeException("Base cluster already defined");
			}

			_tree = (MetaCluster) definition.structure;
		}

		// Add to list of definitions.
		_types.add(definition);
		final int id = _types.indexOf(definition);
		definition.id = id;

		if (identity != null)
		{
			identity.addVersion(identityVersion, definition.id);
		}

		if (relation != null)
		{
			relation.setRelation(relationTag, definition.id);
		}
		// Add class lookup if defined.
		if (definition.clss != null)
		{
			addClass(definition, definition.clss);
		}
		return id;
	}

	private String checkName(final String name) throws TypeException
	{
		if (name == null || name.length() == 0)
		{
			throw new TypeException("invalid parameter");
		}

		return name.toLowerCase();
	}

	private String checkVersion(final String version) throws TypeException
	{
		return checkName(version);
	}

	private boolean isTypeIdInRange(final int id)
	{
		if (_types.size() == 0)
		{
			return false;
		}

		if (id < 0 || id >= _types.size())
		{
			return false;
		}

		return true;
	}

	public int getTypeId(final String name) throws TypeException
	{
		final String checkedName = checkName(name);
		final TypeDefinitionEntry entry = _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name);
		}
		return entry.id;
	}

	public int getTypeId(final String name, final String version) throws TypeException
	{
		final String checkedName = checkName(name);
		final MetaVersion checkedVersion = MetaVersion.parseVersion(checkVersion(version));

		final TypeDefinitionEntry entry = _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name);
		}

		if (!(entry.structure instanceof MetaIdentity))
		{
			throw new TypeException("Structure not an identity");
		}

		final MetaIdentity identity = (MetaIdentity) entry.structure;

		final int id = identity.getVersion(checkedVersion);
		if (id == TypeLibrary.NOTYPE)
		{
			throw new TypeNotDefinedException("Not Defined:" + name + "#" + version);
		}

		return id;
	}

	public int getTypeId(final TypeLocation location) throws TypeException
	{
		if (location == null)
		{
			throw new TypeException("location is null");
		}
		if (location instanceof TypeLocationName)
		{
			final TypeLocationName locationName = (TypeLocationName) location;

			final TypeDefinitionEntry entry = _names.get(checkName(locationName.getName().getFullName()));
			if (entry == null)
			{
				return NOTYPE;
			}
			return entry.id;
		}
		else if (location instanceof TypeLocationDefinition)
		{
			final TypeLocationDefinition locationDef = (TypeLocationDefinition) location;
			if (!isTypeIdInRange(locationDef.getId()))
			{
				return NOTYPE;
			}

			final TypeDefinitionEntry definition = _types.get(locationDef.getId());
			if (definition == null)
			{
				return NOTYPE;
			}

			if (!(definition.structure instanceof MetaIdentity))
			{
				return NOTYPE;
			}

			final MetaIdentity identity = (MetaIdentity) definition.structure;

			return identity.getVersion(locationDef.getVersion());
		}
		else if (location instanceof TypeLocationRelation)
		{
			final TypeLocationRelation locationRelation = (TypeLocationRelation) location;
			if (!isTypeIdInRange(locationRelation.getId()))
			{
				return NOTYPE;
			}

			final TypeDefinitionEntry definition = _types.get(locationRelation.getId());
			if (definition == null)
			{
				return NOTYPE;
			}

			if (!(definition.structure instanceof TypeRelation))
			{
				return NOTYPE;
			}

			final TypeRelation relation = (TypeRelation) definition.structure;

			return relation.getRelation(locationRelation.getTag());
		}
		else if (location instanceof TypeLocationBase)
		{
			if (_primed)
			{
				return 0; // Type 0 must be the base group.
			}
			return -1;
		}
		else
		{
			throw new TypeException("Unknown location type");
		}
	}

	/**
	 * Uses a definition identifier
	 */
	public int getTypeState(final int typeId)
	{
		if (!isTypeIdInRange(typeId))
		{
			return TYPE_NOT_DEFINED;
		}

		final TypeDefinitionEntry definition = _types.get(typeId);
		if (definition == null)
		{
			return TYPE_NOT_DEFINED;
		}
		return definition.state;
	}

	public int getTypeState(final TypeLocation location)
	{
		int id = NOTYPE;
		try
		{
			id = getTypeId(location);
		}
		catch (final TypeException ex)
		{
			return TYPE_NOT_DEFINED;
		}

		return getTypeState(id);
	}

	/**
	 * Register a type. Type must not be defined in the system or reserved.
	 *
	 * requires version.
	 *
	 * @param location
	 * @param structure
	 * @param reader
	 * @param writer
	 * @param clss
	 * @return
	 * @throws TypeException
	 */
	public int register(final TypeLocation location, final TypeElement structure, final TypeLibraryReader reader, final TypeLibraryWriter writer, final Class<?> clss)
			throws TypeException
	{
		// check for valid parameters
		if (structure == null)
		{
			throw new TypeException("invalid parameter: structure is null");
		}
		if (reader == null)
		{
			throw new TypeException("invalid parameter: reader is null");
		}
		if (writer == null)
		{
			throw new TypeException("invalid parameter: writer is null");
		}

		// Check if this has been defined already.
		final int typeId = getTypeId(location);
		final int state = getTypeState(typeId);
		if (state != TYPE_NOT_DEFINED && state != TYPE_RESERVED)
		{
			throw new TypeDefinedException(typeStates[state]);
		}

		TypeDefinitionEntry definition = null;

		if (state == TYPE_NOT_DEFINED)
		{
			definition = new TypeDefinitionEntry();
			definition.structure = structure;
			definition.location = location;
			definition.reader = reader;
			definition.writer = writer;
			definition.clss = clss; // Can be null.
			definition.instantiator = new TypeClassInstantiator(clss);
			definition.state = TYPE_COMPLETE;

			add(definition);
		}
		else if (state == TYPE_RESERVED)
		{
			definition = _types.get(typeId);
			definition.location = location;
			definition.structure = structure;
			definition.reader = reader;
			definition.writer = writer;
			definition.clss = clss;
			definition.instantiator = new TypeClassInstantiator(clss);
			definition.state = TYPE_COMPLETE;

			// copied from add. Need to add class.
			if (definition.clss != null)
			{
				addClass(definition, definition.clss);
			}
		}
		else
		{
			throw new TypeException("invalid state");
		}

		// Bind the definitions to the library.
		structure.bind(this, definition.id, location, structure);
		if (reader instanceof TypeBound)
		{
			((TypeBound) reader).bind(this, definition.id, structure);
		}

		if (writer instanceof TypeBound)
		{
			((TypeBound) writer).bind(this, definition.id, structure);
		}

		return definition.id;
	}

	/**
	 *
	 * requires version.
	 *
	 * Register a name with its structure. Used when reading from a dictionary
	 * file.
	 */
	public int register(final TypeLocation location, final TypeElement structure) throws TypeException
	{
		if (location == null)
		{
			throw new TypeException("invalid parameter");
		}
		if (structure == null)
		{
			throw new TypeException("invalid parameter");
		}

		final int typeId = getTypeId(location);
		if (typeId != NOTYPE)
		{
			throw new TypeDefinedException("type registered at location");
		}

		TypeDefinitionEntry definition = null;

		definition = new TypeDefinitionEntry();
		definition.location = location;
		definition.structure = structure;
		definition.reader = null;
		definition.writer = null;
		definition.clss = null; // Can be null.
		definition.state = TYPE_REGISTERED;

		add(definition);

		try
		{
			structure.bind(this, definition.id, location, structure);
		}
		catch (final TypeException e)
		{
			throw new TypeException("Failed to bind", e);
		}
		return definition.id;
	}

	public int reserve(final TypeLocation location) throws TypeException
	{
		if (location == null)
		{
			throw new TypeException("invalid parameter");
		}

		final int typeId = getTypeId(location);
		if (typeId != NOTYPE)
		{
			throw new TypeDefinedException("type registered at location");
		}

		TypeDefinitionEntry definition = null;

		definition = new TypeDefinitionEntry();
		definition.location = location;
		definition.structure = null;
		definition.reader = null;
		definition.writer = null;
		definition.clss = null; // Can be null.
		definition.state = TYPE_RESERVED;

		add(definition);

		return definition.id;
	}

	public int bind(final int typeId, final TypeElement structure) throws TypeException
	{
		if (structure == null)
		{
			throw new TypeException("invalid parameter");
		}

		final int state = getTypeState(typeId);
		if (state != TYPE_RESERVED)
		{
			throw new TypeException("type in wrong state:" + typeStates[state]);
		}

		final TypeDefinitionEntry definition = _types.get(typeId);
		definition.structure = structure;
		definition.state = TYPE_REGISTERED;

		try
		{
			structure.bind(this, definition.id, definition.location, structure);
		}
		catch (final TypeException e)
		{
			throw new TypeException("Failed to bind", e);
		}
		return definition.id;

	}

	/*
	 * requires version
	 */
	public int bind(final int typeId, final TypeLibraryReader reader, final TypeLibraryWriter writer, final Class<?> clss) throws TypeException
	{
		final int state = getTypeState(typeId);
		if (state != TYPE_REGISTERED)
		{
			throw new TypeException("type in wrong state:" + typeStates[state]);
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		def.reader = reader;
		def.writer = writer;
		def.clss = clss;
		if (def.instantiator == null)
		{
			def.instantiator = new TypeClassInstantiator(clss);
		}
		def.state = TYPE_COMPLETE;

		// Need to add class.
		if (def.clss != null)
		{
			addClass(def, def.clss);
		}
		final TypeElement structure = def.structure;

		structure.bind(this, def.id, def.location, structure);

		if (reader instanceof TypeBound)
		{
			((TypeBound) reader).bind(this, def.id, structure);
		}

		if (writer instanceof TypeBound)
		{
			((TypeBound) writer).bind(this, def.id, structure);
		}

		return def.id;
	}

	public int bind(final int typeId, final Class<?> clss) throws TypeException
	{
		final ArgotMarshaller marshallerType = clss.getAnnotation(ArgotMarshaller.class);
		if (marshallerType == null)
		{
			throw new TypeException("Error: ArgotMarshaller annotation not set for " + clss.getName());
		}
		try
		{
			final TypeLibraryReaderWriter marshaller = marshallerType.value().newInstance();
			return bind(typeId, marshaller, marshaller, clss);
		}
		catch (final Exception ex)
		{
			throw new TypeException("Error: Failed to create instance of marshaller: " + marshallerType.value().getName(), ex);
		}

	}

	/*
	 * returns the structure identifier
	 */

	public int getDefinitionId(final MetaName name, final MetaVersion version) throws TypeException
	{
		return getDefinitionId(name.getFullName(), version.toString());
	}

	public int getDefinitionId(final String name, final String version) throws TypeException
	{
		final String checkedName = checkName(name);
		final MetaVersion checkedVersion = MetaVersion.parseVersion(checkVersion(version));

		final TypeDefinitionEntry entry = _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name);
		}

		if (!(entry.structure instanceof MetaIdentity))
		{
			throw new TypeException("name not meta.identity");
		}

		final MetaIdentity identity = (MetaIdentity) entry.structure;
		final int id = identity.getVersion(checkedVersion);
		if (id == -1)
		{
			throw new TypeException("version not found");
		}

		return id;
	}

	/**
	 * Using a definition Id, find the associated name.
	 *
	 * @param typeId
	 * @return
	 * @throws TypeNotDefinedException
	 */
	public MetaName getName(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeNotDefinedException("type not found");
		}

		if (def.location instanceof TypeLocationName)
		{
			return ((TypeLocationName) def.location).getName();
		}
		else if (def.location instanceof TypeLocationDefinition)
		{
			return getName(((TypeLocationDefinition) def.location).getId());
		}
		else if (def.location instanceof TypeLocationRelation)
		{
			return getName(((TypeLocationRelation) def.location).getId());
			// String tag = ((TypeLocationRelation)def.location).getTag();
			// return MetaName.parseName( name.toString() + "." + tag );
		}
		else if (def.location instanceof TypeLocationBase)
		{
			// This is a bit ugly.
			return new MetaName(0, "...", "...");
			// throw new TypeException("Library base not named");
		}

		throw new TypeException("Type not named");
	}

	public int getTypeId(final MetaName name) throws TypeException
	{
		return getTypeId(name.getFullName());
	}

	public MetaVersion getVersion(final int definitionId) throws TypeException
	{
		if (!isTypeIdInRange(definitionId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(definitionId);
		if (def == null)
		{
			throw new TypeNotDefinedException("type not found");
		}

		if (!(def.location instanceof TypeLocationDefinition))
		{
			throw new TypeException("Not a definition type");
		}

		return ((TypeLocationDefinition) def.location).getVersion();
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public boolean isSimpleType(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry nameEntry = _types.get(typeId);
		if (nameEntry == null)
		{
			throw new TypeNotDefinedException("type not found");
		}

		return nameEntry.isSimple;
	}

	/**
	 *
	 * @param typeId
	 * @param isSimple
	 * @throws TypeException
	 */
	public void setSimpleType(final int typeId, final boolean isSimple) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry nameEntry = _types.get(typeId);
		if (nameEntry == null)
		{
			throw new TypeNotDefinedException("type not found");
		}

		nameEntry.isSimple = isSimple;
	}

	public TypeLocation getLocation(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeNotDefinedException("type id");
		}

		return def.location;
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public TypeElement getStructure(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeNotDefinedException("type id");
		}

		if (def.state != TYPE_COMPLETE && def.state != TYPE_REGISTERED)
		{
			throw new TypeException("type not complete: " + this.getName(typeId).getFullName());
		}

		return def.structure;
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public TypeLibraryReader getReader(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeNotDefinedException("type id");
		}

		if (def.state != TYPE_COMPLETE)
		{
			throw new TypeException("type not complete: " + this.getName(typeId).getFullName());
		}

		return def.reader;
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public TypeLibraryWriter getWriter(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeException("type not found");
		}

		if (def.state != TYPE_COMPLETE)
		{
			throw new TypeException("type not complete: " + typeId + " - " + this.getName(typeId).getFullName());
		}

		return def.writer;
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public Class<?> getClass(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeException("type not found");
		}

		if (def.clss == null)
		{
			throw new TypeException("no class bound for type.");
		}

		return def.clss;
	}

	/**
	 *
	 * @param typeId
	 * @return
	 * @throws TypeException
	 */
	public TypeInstantiator getInstantiator(final int typeId) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeException("type not found");
		}

		if (def.instantiator == null)
		{
			throw new TypeException("no class bound for type.");
		}

		return def.instantiator;
	}

	/**
	 *
	 * @param clss
	 * @return structure id
	 * @throws TypeException
	 */

	public int[] getId(final Class<?> clss) throws TypeException
	{
		Class<?> searchClass = clss;

		if (clss.getName().equals("long"))
		{
			searchClass = Long.class;
		}
		if (clss.getName().equals("short"))
		{
			searchClass = Short.class;
		}
		if (clss.getName().equals("byte"))
		{
			searchClass = Byte.class;
		}
		if (clss.getName().equals("boolean"))
		{
			searchClass = Boolean.class;
		}
		if (clss.getName().equals("int"))
		{
			searchClass = Integer.class;
		}
		if (clss.getName().equals("double"))
		{
			searchClass = Double.class;
		}
		if (clss.getName().equals("float"))
		{
			searchClass = Float.class;
		}

		final List<TypeDefinitionEntry> list = _classes.get(searchClass);
		if (list == null)
		{
			throw new TypeException("no id for class: " + searchClass.getName());
		}

		final int[] values = new int[list.size()];
		final Iterator<TypeDefinitionEntry> iter = list.iterator();
		int x = 0;
		while (iter.hasNext())
		{
			values[x] = iter.next().id;
			x++;
		}
		return values;
	}

	/**
	 *
	 * @param typeId
	 * @param clss
	 * @throws TypeException
	 */
	public void addClassAlias(final int typeId, final Class<?> clss) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeException("type not found");
		}

		addClass(def, clss);
	}

	public void setTypeInstantiator(final int typeId, final TypeInstantiator instantiator) throws TypeException
	{
		if (!isTypeIdInRange(typeId))
		{
			throw new TypeNotDefinedException("type id not in range");
		}

		final TypeDefinitionEntry def = _types.get(typeId);
		if (def == null)
		{
			throw new TypeException("type not found");
		}

		def.instantiator = instantiator;
	}

	public Set<String> getNames()
	{
		return _names.keySet();
	}
}