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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import com.argot.meta.MetaIdentity;
import com.argot.meta.MetaName;
import com.argot.meta.MetaVersion;

/**
 * The TypeLibrary is central to Argot.  It provides the collection of definitions in any system.
 * 
 * A TypeLibrary has a collection of names, collection of definitions (many to one name), and
 * associates classes to definitions (one to one).  The interface allows looking up based on 
 * names, definition id's or classes.
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
    
    public static final String[] typeStates = {"Type not defined","type reserved","type registered","type complete"};
    
	private ArrayList _types;    // index(nameId) to TypeNameEntry
	
	private HashMap _names;  // name to TypeNameEntry

	private HashMap _classes;  // class to TypeDefinitionEntry
	
	private class TypeDefinitionEntry
	{
	    public int state;
	    public int id;
		public String version;
		public TypeLibraryReader reader;
		public TypeLibraryWriter writer;
		public Class clss;
		public TypeLocation location;
		public TypeElement structure;
		public boolean isSimple;
	}
	
	public TypeLibrary()
	{
		System.out.println("\nArgot Version 1.3.a");
		System.out.println("Copyright 2003-2009 (C) Live Media Pty Ltd.");
		System.out.println("www.einet.com.au\n");
		
		_types = new ArrayList();
		_names = new HashMap();
		_classes = new HashMap();
	}

	public TypeLibrary( TypeLibraryLoader[] loaders )
	throws TypeException
	{
		this();
		
		for(int x=0;x<loaders.length;x++)
		{
			loadLibrary(loaders[x]);
		}
	}

	public void loadLibrary( TypeLibraryLoader loader ) 
	throws TypeException
	{
		loader.load(this);
	}
	
	/**
	 * Add a new definition to the library. This must be a fresh entry.
	 * Either a reserved or register call.
	 * @param definition
	 * @return
	 */
	private int add( TypeDefinitionEntry definition ) 
	throws TypeException
	{
		MetaIdentity identity = null;
		MetaVersion identityVersion = null;
		TypeRelation relation = null;
		String relationTag = null;
		
	    // Add to list of names if it isn't already there.
	    if (definition.location instanceof TypeLocationName)
	    {
	    	String name = checkName( ((TypeLocationName)definition.location).getName().toString() );
	    	
		    TypeDefinitionEntry nameEntry = (TypeDefinitionEntry) _names.get(name);
		    if ( nameEntry != null )
		    {
		    	throw new TypeDefinedException("Type name already exists");
		    }
		    
		    // Add name lookup in names map.
		    _names.put(name, definition);
		}
	    else if (definition.location instanceof TypeLocationDefinition)
	    {
	    	TypeLocationDefinition typeDef = (TypeLocationDefinition) definition.location;
	    	
	    	//String typeName = this.getName(tv.getTypeId()) + "#" + checkVersion( tv.getVersion() );
	    	if ( !isTypeIdInRange( typeDef.getId() ) )
	    	{
	    		throw new TypeException("Type not in range");
	    	}
	    	
	    	TypeDefinitionEntry nameEntry = (TypeDefinitionEntry) _types.get(typeDef.getId());
	    	if ( nameEntry == null )
	    	{
	    		throw new TypeNotDefinedException("Type name not defined");
	    	}
		    	
		    if ( !( nameEntry.structure instanceof MetaIdentity ) )
		    {
		    	throw new TypeException("Location not a name type");
		    }
		    
		    identity = (MetaIdentity) nameEntry.structure;
		    identityVersion = typeDef.getVersion();
	    }
	    else if (definition.location instanceof TypeLocationRelation)
	    {
	    	TypeLocationRelation typeRel = (TypeLocationRelation) definition.location;
	    	
	    	if ( !isTypeIdInRange( typeRel.getId() ) )
	    	{
	    		throw new TypeException("Type not in range");
	    	}
	    	
	    	TypeDefinitionEntry targetEntry = (TypeDefinitionEntry) _types.get(typeRel.getId());
	    	if ( targetEntry == null )
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

	    // Add to list of definitions.
	    _types.add( definition );
	    int id = _types.indexOf( definition );
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
	    if ( definition.clss != null )
	    {
	        if ( _classes.get( definition.clss) != null )
	            throw new TypeException("class overloaded");
	        _classes.put( definition.clss, definition );
	    }
	    return id;
	}

	private String checkName( String name )
	throws TypeException
	{
	    if ( name == null || name.length() == 0 )
	        throw new TypeException("invalid parameter");
	    
	    return name.toLowerCase();
	}
	
	private String checkVersion(String version )
	throws TypeException
	{
		return checkName(version);
	}
	
	private boolean isTypeIdInRange( int id )
	{
		if ( _types.size() == 0 )
			return false;
		
		if ( id < 0 || id >= _types.size() )
			return false;
		
		return true;
	}
	
	public int getTypeId(String name )
	throws TypeException
	{
		String checkedName = checkName(name);
		TypeDefinitionEntry entry = (TypeDefinitionEntry) _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name );
		}
		return entry.id;
	}
	
	public int getTypeId(String name, String version)
	throws TypeException
	{
		String checkedName = checkName(name);
		MetaVersion checkedVersion = MetaVersion.parseVersion(checkVersion(version));

		TypeDefinitionEntry entry = (TypeDefinitionEntry) _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name );
		}

		if ( !(entry.structure instanceof MetaIdentity))
		{
			throw new TypeException("Structure not an identity");
		}
		
		MetaIdentity identity = (MetaIdentity) entry.structure;
		
		int id = identity.getVersion(checkedVersion);
		if ( id == TypeLibrary.NOTYPE )
		{
			throw new TypeNotDefinedException("Not Defined:" + name + "#" + version);
		}
		
		return id;
	}
	
	public int getTypeId( TypeLocation location )
	throws TypeException
	{
		if ( location instanceof TypeLocationName )
		{
			TypeLocationName locationName = (TypeLocationName) location;
			
			TypeDefinitionEntry entry = (TypeDefinitionEntry) _names.get(checkName(locationName.getName().toString()));
			if (entry == null)
			{
				return NOTYPE;
			}
			return entry.id;
		}
		else if ( location instanceof TypeLocationDefinition )
		{
			TypeLocationDefinition locationDef = (TypeLocationDefinition) location;
	    	if ( !isTypeIdInRange(locationDef.getId()) ) 
	    		return NOTYPE;
	    	
	        TypeDefinitionEntry definition = (TypeDefinitionEntry) _types.get( locationDef.getId() );
			if (definition == null)
				return NOTYPE;
			
			if (!(definition.structure instanceof MetaIdentity))
			{
				return NOTYPE;
			}
			
			MetaIdentity identity = (MetaIdentity) definition.structure;
			
			return identity.getVersion(locationDef.getVersion());
		}
		else if (location instanceof TypeLocationRelation )
		{
			TypeLocationRelation locationRelation = (TypeLocationRelation) location;
	    	if ( !isTypeIdInRange(locationRelation.getId()) ) 
	    		return NOTYPE;
	    	
	        TypeDefinitionEntry definition = (TypeDefinitionEntry) _types.get( locationRelation.getId() );
			if (definition == null)
				return NOTYPE;
			
			if (!(definition.structure instanceof TypeRelation))
			{
				return NOTYPE;
			}
			
			TypeRelation relation = (TypeRelation) definition.structure;
			
			return relation.getRelation(locationRelation.getTag());
		}
		else
		{
			throw new TypeException("Unknown location type");
		}
	}
		
    /**
     * Uses a definition identifier
     */
    public int getTypeState(int typeId)
    {
    	if ( !isTypeIdInRange(typeId) ) 
    		return TYPE_NOT_DEFINED;
    	
        TypeDefinitionEntry definition = (TypeDefinitionEntry) _types.get( typeId );
        if ( definition == null )
            return TYPE_NOT_DEFINED;
        return definition.state;
    }
    
    public int getTypeState( TypeLocation location )
    {
    	int id = NOTYPE;
    	try
    	{
    		id = getTypeId(location);
    	}
    	catch( TypeException ex)
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
     * @param name
     * @param reader
     * @param writer
     * @param clss
     * @param structure
     * @return
     * @throws TypeException
     */
	public int register( TypeLocation location, TypeElement structure, TypeLibraryReader reader, TypeLibraryWriter writer, Class clss )
	throws TypeException
	{
		// check for valid parameters
	    if ( structure == null )
	        throw new TypeException("invalid parameter: structure is null");
	    if ( reader == null )
	        throw new TypeException("invalid parameter: reader is null");
	    if ( writer == null )
	        throw new TypeException("invalid parameter: writer is null");
	    
	    // Check if this has been defined already.
	    int typeId = getTypeId( location );	    
	    int state = getTypeState( typeId);
	    if ( state != TYPE_NOT_DEFINED && state != TYPE_RESERVED )
	    {
	        throw new TypeDefinedException( typeStates[state] );
	    }
	    
	    TypeDefinitionEntry definition = null;
	    
	    if ( state == TYPE_NOT_DEFINED )
	    {
		    definition = new TypeDefinitionEntry();
		    definition.structure = structure;
		    definition.location = location;
		    definition.reader = reader;
		    definition.writer = writer;
		    definition.clss = clss;  // Can be null.
		    definition.state = TYPE_COMPLETE;
		    	    
		    add( definition );
	    }
	    else if ( state == TYPE_RESERVED )
	    {
		    definition = (TypeDefinitionEntry) _types.get( typeId );
		    definition.location = location;
		    definition.structure = structure;		    
		    definition.reader = reader;
		    definition.writer = writer;
		    definition.clss = clss;
		    definition.state = TYPE_COMPLETE;
		    
		    // copied from add.  Need to add class.
		    if ( definition.clss != null )
		    {
		        if ( _classes.get( definition.clss) != null )
		            throw new TypeException("class overloaded");
		        _classes.put( definition.clss, definition );
		    }
	    }
	    else
	    {
	        throw new TypeException("invalid state");
	    }
	    
	    // Bind the definitions to the library.
    	structure.bind( this, definition.id, location, structure );        
	    if ( reader instanceof TypeBound )
	    {
	    	((TypeBound)reader).bind(this, definition.id, structure);
	    }
	    
	    if ( writer instanceof TypeBound )
	    {
	    	((TypeBound)writer).bind(this, definition.id, structure );
	    }
	    
	    return definition.id;
	}

	
	
	/**
	 * 
	 * requires version.
	 * 
	 * Register a name with its structure.  Used when reading from
	 * a dictionary file.
	 */
	public int register( TypeLocation location, TypeElement structure )
	throws TypeException
	{
	    if ( location == null )
	    	throw new TypeException("invalid parameter");
	    if ( structure == null )
	        throw new TypeException("invalid parameter");
	    
	    int typeId = getTypeId( location );
	    if ( typeId != NOTYPE  )
	    {
	        throw new TypeDefinedException( "type registered at location" );
	    }
	    
	    TypeDefinitionEntry definition = null;
	    
	    definition = new TypeDefinitionEntry();
	    definition.location = location;
	    definition.structure = structure;		    
	    definition.reader = null;
	    definition.writer = null;
	    definition.clss = null;  // Can be null.
	    definition.state = TYPE_REGISTERED;
	    	    
	    add( definition );

	    try
	    {
	    	structure.bind( this, definition.id, location, structure );
	    }
	    catch (TypeException e)
	    {
	    	throw new TypeException("Failed to bind", e);
	    }
	    return definition.id;
	}

	public int reserve( TypeLocation location )
	throws TypeException
	{
	    if ( location == null )
	    	throw new TypeException("invalid parameter");
	    
	    int typeId = getTypeId( location );
	    if ( typeId != NOTYPE  )
	    {
	        throw new TypeDefinedException( "type registered at location" );
	    }
	    
	    TypeDefinitionEntry definition = null;
	    
	    definition = new TypeDefinitionEntry();
	    definition.location = location;
	    definition.structure = null;		    
	    definition.reader = null;
	    definition.writer = null;
	    definition.clss = null;  // Can be null.
	    definition.state = TYPE_RESERVED;
	    	    
	    add( definition );

	    return definition.id;
	}
	
	public int bind( int typeId, TypeElement structure )
	throws TypeException
	{
	    if ( structure == null )
	        throw new TypeException("invalid parameter");
		
	    int state = getTypeState( typeId );
	    if ( state != TYPE_RESERVED )
	        throw new TypeException("type in wrong state:" + typeStates[state]);
		
	    TypeDefinitionEntry definition = (TypeDefinitionEntry) _types.get( typeId );
	    definition.structure = structure;
	    definition.state = TYPE_REGISTERED;

	    try
	    {
	    	structure.bind( this, definition.id, definition.location, structure );
	    }
	    catch (TypeException e)
	    {
	    	throw new TypeException("Failed to bind", e);
	    }
	    return definition.id;
	    
	}
	
	/*
	 * requires version
	 */
	public int bind( int typeId, TypeLibraryReader reader, TypeLibraryWriter writer, Class clss )
	throws TypeException
	{
	    int state = getTypeState( typeId );
	    if ( state != TYPE_REGISTERED )
	        throw new TypeException("type in wrong state:" + typeStates[state]);
	    
	    TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
	    def.reader = reader;
	    def.writer = writer;
	    def.clss = clss;
	    def.state = TYPE_COMPLETE;

	    // copied from add.  Need to add class.
	    if ( def.clss != null )
	    {
	        if ( _classes.get( def.clss) != null )
	            throw new TypeException("class overloaded: ");
	        _classes.put( def.clss, def );
	    }
	    TypeElement structure = (TypeElement) def.structure;
	    
	    structure.bind( this, def.id, null, structure );
	    
	    if ( reader instanceof TypeBound )
	    {
	    	((TypeBound)reader).bind(this, def.id, structure );
	    }
	    
	    if ( writer instanceof TypeBound )
	    {
	    	((TypeBound)writer).bind(this, def.id, structure );
	    }
	    
	    return def.id;
	}

	/*
	 * returns the structure identifier
	 */
	
	public int getDefinitionId(MetaName name, MetaVersion version) 
	throws TypeException
	{
		return getDefinitionId(name.toString(), version.toString());
	}
	
	public int getDefinitionId( String name, String version )
	throws TypeException
	{
	    String checkedName = checkName( name );
	    MetaVersion checkedVersion = MetaVersion.parseVersion(checkVersion( version ));
	    
		TypeDefinitionEntry entry = (TypeDefinitionEntry) _names.get(checkedName);
		if (entry == null)
		{
			throw new TypeNotDefinedException("Not Defined:" + name );
		}
	    
		if (!(entry.structure instanceof MetaIdentity))
		{
			throw new TypeException("name not meta.identity");
		}
		
		MetaIdentity identity = (MetaIdentity) entry.structure;
		int id = identity.getVersion(checkedVersion);
	    if ( id == -1 )
	    	throw new TypeException("version not found");
	    
	    return id;
	}
	

	/**
	 * Using a definition Id, find the associated name.
	 * 
	 * @param systemId
	 * @return
	 * @throws TypeNotDefinedException 
	 */
	public MetaName getName(int typeId)
	throws TypeException 
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");
		
		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeNotDefinedException( "type not found" );
		
		if (def.location instanceof TypeLocationName)
		{
			return ((TypeLocationName)def.location).getName();
		}
		else if (def.location instanceof TypeLocationDefinition)
		{
			return getName(((TypeLocationDefinition)def.location).getId());
		}
		else if (def.location instanceof TypeLocationRelation)
		{
			return getName(((TypeLocationRelation)def.location).getId());// + "#" + ((TypeLocationRelation)def.location).getTag();
		}
				
		throw new TypeException("Type not named");
	}
	
	public int getTypeId(MetaName name) 
	throws TypeException
	{
		return getTypeId(name.toString());
	}
	
	
	public String getVersion(int definitionId) 
	throws TypeException
	{
		if ( !isTypeIdInRange(definitionId) )
			throw new TypeNotDefinedException( "type id not in range");
		
		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( definitionId );
		if ( def == null )
			throw new TypeNotDefinedException( "type not found" );
		
		return def.version;
	}
	
	/**
	 * 
	 * @param name id
	 * @return
	 * @throws TypeException
	 */
	public boolean isSimpleType( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");
		
		TypeDefinitionEntry nameEntry = (TypeDefinitionEntry) _types.get( typeId );
		if ( nameEntry == null )
			throw new TypeNotDefinedException( "type not found" );
		
		return nameEntry.isSimple;
	}
	
	/**
	 * 
	 * @param name id
	 * @param isSimple
	 * @throws TypeException
	 */
	public void setSimpleType( int typeId, boolean isSimple )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");
		
		TypeDefinitionEntry nameEntry = (TypeDefinitionEntry) _types.get( typeId );
		if ( nameEntry == null )
			throw new TypeNotDefinedException( "type not found" );
		
		nameEntry.isSimple = isSimple;
	}

	public TypeLocation getLocation( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeNotDefinedException( "type id" );

		return def.location;		
	}
	
	/**
	 * 
	 * @param structure id
	 * @return
	 * @throws TypeException
	 */
	public TypeElement getStructure( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeNotDefinedException( "type id" );
		
		if ( def.state != TYPE_COMPLETE && def.state != TYPE_REGISTERED )
			throw new TypeException( "type not complete: " + this.getName(typeId));

		return (TypeElement) def.structure;
	}
	
    /**
     * 
     * @param structure id
     * @return
     * @throws TypeException
     */
	public TypeLibraryReader getReader( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def =  (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeNotDefinedException( "type id" );
			
		if ( def.state != TYPE_COMPLETE )
			throw new TypeException( "type not complete: " + this.getName(typeId));
			
		return def.reader;
	}
	
	/**
	 * 
	 * @param structure id
	 * @return
	 * @throws TypeException
	 */
	public TypeLibraryWriter getWriter( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeException( "type not found" );
				
		if ( def.state != TYPE_COMPLETE )
			throw new TypeException( "type not complete: " + typeId + " - " + this.getName(typeId));
			
		return def.writer;
	}

	/**
	 * 
	 * @param structure id
	 * @return
	 * @throws TypeException
	 */
	public Class getClass( int typeId )
	throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def = (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeException( "type not found" );

		if ( def.clss == null )
			throw new TypeException( "no class bound for type." );
			
		return def.clss;
	} 	

	/**
	 * 
	 * @param clss
	 * @return structure id
	 * @throws TypeException
	 */
	public int getId( Class clss )
	throws TypeException
	{
		Class searchClass = clss;
		
		if ( clss.getName().equals( "long") )
			searchClass = Long.class;
		if ( clss.getName().equals( "short" ) )
			searchClass = Short.class;
		if ( clss.getName().equals( "byte" ) )
			searchClass = Byte.class;
		if ( clss.getName().equals( "boolean" ) )
			searchClass = Boolean.class;
		if ( clss.getName().equals( "int" ))
			searchClass = Integer.class;
		if ( clss.getName().equals( "double" ))
			searchClass = Double.class;
		if ( clss.getName().equals( "float" ))
			searchClass = Float.class;	
		
			
		TypeDefinitionEntry w = (TypeDefinitionEntry) _classes.get( searchClass );
		if ( w == null )
			throw new TypeException( "no id for class: " + searchClass.getName() );
		
		if ( w.id == -1 )
			throw new TypeException( "no id for class");
			
		return w.id;
	}

	/**
	 * 
	 * @param structure id
	 * @param clss
	 * @throws TypeException
	 */
	public void addClassAlias( int typeId, Class clss ) throws TypeException
	{
		if ( !isTypeIdInRange(typeId) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinitionEntry def =  (TypeDefinitionEntry) _types.get( typeId );
		if ( def == null )
			throw new TypeException( "type not found" );
		
		TypeDefinitionEntry w = (TypeDefinitionEntry) _classes.get( clss );
		if ( w != null )
			throw new TypeException( "class overloaded: " + clss.getName() );

		_classes.put( clss, def );
	}
	

    public Set getNames()
    {
    	return _names.keySet();
    }
}