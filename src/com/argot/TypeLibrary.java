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
package com.argot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class TypeLibrary
{
    public static int NOTYPE = -1;
    
    public static int TYPE_NOT_DEFINED = 0;
    public static int TYPE_RESERVED = 1;
    public static int TYPE_REGISTERED = 2;
    public static int TYPE_COMPLETE = 3;
    
	private ArrayList _types;
	private HashMap _names;
	private HashMap _classes;
	
	private class TypeDefinition
	{
	    public int state;
	    public int id;
		public String name;
		public TypeReader reader;
		public TypeWriter writer;
		public Class clss;
		public TypeElement structure;
	}
	
	public TypeLibrary()
	{
		System.out.println("");
		System.out.println("Argot Version 1.2.0");
		System.out.println("Copyright 2003-2005 (C) Live Media Pty Ltd.");
		System.out.println("www.einet.com.au");
		
		_types = new ArrayList();
		_names = new HashMap();
		_classes = new HashMap();
	}

	/**
	 * Add a new definition to the library. This must be a fresh entry.
	 * Either a reserved or register call.
	 * @param definition
	 * @return
	 */
	private int add( TypeDefinition definition ) 
	throws TypeException
	{
	    _types.add( definition );
	    int id = _types.indexOf( definition );
	    definition.id = id;
	    _names.put( definition.name, definition );
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
	
	private boolean isTypeIdInRange( int id )
	{
		if ( id < 0 || id > _types.size() )
			return false;
		
		return true;
	}
	/**
	 * Returns the current state of the specific type.
	 * 
     * @param string
     */
    public int getTypeState(String uncheckedName)
    {
        String name;
        try
        {
            name = checkName( uncheckedName );
        } 
        catch (TypeException e)
        {
            return TYPE_NOT_DEFINED;
        }
        
        TypeDefinition definition = (TypeDefinition) _names.get( name );
        if ( definition == null )
            return TYPE_NOT_DEFINED;
        return definition.state;
    }

    public int getTypeState(int id)
    {
    	if ( !isTypeIdInRange(id) ) 
    		return TYPE_NOT_DEFINED;
    	
        TypeDefinition definition = (TypeDefinition) _types.get( id );
        if ( definition == null )
            return TYPE_NOT_DEFINED;
        return definition.state;
    }

    /**
     * Register a type. Type must not be defined in the system or reserved.
     * 
     * @param name
     * @param reader
     * @param writer
     * @param clss
     * @param structure
     * @return
     * @throws TypeException
     */
	public int register( String uncheckedName, TypeElement structure, TypeReader reader, TypeWriter writer, Class clss )
	throws TypeException
	{
	    String name = checkName( uncheckedName );
	    
	    if ( structure == null )
	        throw new TypeException("invalid parameter: structure is null");
	    if ( reader == null )
	        throw new TypeException("invalid parameter: reader is null");
	    if ( writer == null )
	        throw new TypeException("invalid parameter: writer is null");
	    
	    int state = getTypeState( name );
	    if ( state != TYPE_NOT_DEFINED && state != TYPE_RESERVED )
	    {
	        throw new TypeNameDefinedException( name + " " + state );
	    }
	    
	    TypeDefinition definition = null;
	    
	    if ( state == TYPE_NOT_DEFINED )
	    {
		    definition = new TypeDefinition();
		    definition.name = name;
		    definition.structure = structure;
		    definition.reader = reader;
		    definition.writer = writer;
		    definition.clss = clss;  // Can be null.
		    definition.state = TYPE_COMPLETE;
		    	    
		    add( definition );
	    }
	    else if ( state == TYPE_RESERVED )
	    {
		    definition = (TypeDefinition) _names.get( name );
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
	    
	    structure.bind( this, structure, name, definition.id );

	    return definition.id;
	}

	/**
	 * Register a name with its structure.  Used when reading from
	 * a dictionary file.
	 */
	public int register( String uncheckedName, TypeElement structure )
	throws TypeException
	{
	    String name = checkName( uncheckedName );
	    
	    if ( structure == null )
	        throw new TypeException("invalid parameter");
	    
	    int state = getTypeState( name );
	    if ( state != TYPE_NOT_DEFINED && state != TYPE_RESERVED  )
	    {
	        throw new TypeNameDefinedException( name );
	    }
	    
	    TypeDefinition definition = null;
	    
	    if ( state == TYPE_NOT_DEFINED )
	    {
		    definition = new TypeDefinition();
		    definition.name = name;
		    definition.structure = structure;
		    definition.reader = null;
		    definition.writer = null;
		    definition.clss = null;  // Can be null.
		    definition.state = TYPE_REGISTERED;
		    	    
		    add( definition );
	    }
	    else if ( state == TYPE_RESERVED )
	    {
		    definition = (TypeDefinition) _names.get( name );
		    definition.structure = structure;
		    definition.state = TYPE_REGISTERED;
		    
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
	    
	    structure.bind( this, structure, name, definition.id );
	    
	    return definition.id;
	}
	
	/**
	 * Reserve a name.  Used for circular references.
	 * 
	 * @param name
	 * @return
	 * @throws TypeException
	 */
	public int reserve( String uncheckedName )
	throws TypeException
	{
	    String name = checkName( uncheckedName );
	    
	    if ( getTypeState( name ) != TYPE_NOT_DEFINED )
	    {
	        throw new TypeNameDefinedException( name );
	    }
	    
	    TypeDefinition definition = new TypeDefinition();
	    definition.name = name;
	    definition.structure = null;
	    definition.reader = null;
	    definition.writer = null;
	    definition.clss = null;  // Can be null.
	    definition.state = TYPE_RESERVED;
	    	    
	    int id = add( definition );
	    return id;	    
	}

	public int bind( String uncheckedName, TypeReader reader, TypeWriter writer, Class clss )
	throws TypeException
	{
		String name  = checkName( uncheckedName );
		
	    int state = getTypeState( name );
	    if ( state != TYPE_REGISTERED )
	        throw new TypeException("type in wrong state:" + state);
	    
	    TypeDefinition def = (TypeDefinition) _names.get( name );
	    def.reader = reader;
	    def.writer = writer;
	    def.clss = clss;
	    def.state = TYPE_COMPLETE;

	    // copied from add.  Need to add class.
	    if ( def.clss != null )
	    {
	        if ( _classes.get( def.clss) != null )
	            throw new TypeException("class overloaded: " + name );
	        _classes.put( def.clss, def );
	    }
	    
	    def.structure.bind( this, def.structure, name, def.id );
	    
	    return def.id;
	}

	public int getId( String uncheckedName )
	throws TypeException
	{
	    String name = checkName( uncheckedName );
		TypeDefinition def = (TypeDefinition) _names.get( name );
		if ( def == null )
			throw new TypeNotDefinedException( name );
			
		return def.id;
	}
	
	public String getName( int id )
	throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");
		
		TypeDefinition def = (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeNotDefinedException( "type not found" );
		
		return def.name;
	}	

	public TypeElement getStructure( int id )
	throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinition def = (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeNotDefinedException( "type id" );
		
		if ( def.state != TYPE_COMPLETE && def.state != TYPE_REGISTERED )
			throw new TypeException( "type not complete: " + this.getName(id));

		return def.structure;
	}
	

	public TypeReader getReader( int id )
	throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinition def =  (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeNotDefinedException( "type id" );
			
		if ( def.state != TYPE_COMPLETE )
			throw new TypeException( "type not complete: " + this.getName(id));
			
		return def.reader;
	}
	
	public TypeWriter getWriter( int id )
	throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinition def = (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeException( "type not found" );
				
		if ( def.state != TYPE_COMPLETE )
			throw new TypeException( "type not complete: " + this.getName(id));
			
		return def.writer;
	}

	public Class getClass( int id )
	throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinition def = (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeException( "type not found" );

		if ( def.clss == null )
			throw new TypeException( "no class bound for type: " + def.name );
			
		return def.clss;
	} 	

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
		
			
		TypeDefinition w = (TypeDefinition) _classes.get( searchClass );
		if ( w == null )
			throw new TypeException( "no id for class: " + searchClass.getName() );
		
		if ( w.id == -1 )
			throw new TypeException( "no id for class");
			
		return w.id;
	}

	public void addClassAlias( int id, Class clss ) throws TypeException
	{
		if ( !isTypeIdInRange(id) )
			throw new TypeNotDefinedException( "type id not in range");

		TypeDefinition def =  (TypeDefinition) _types.get( id );
		if ( def == null )
			throw new TypeException( "type not found" );
		
		TypeDefinition w = (TypeDefinition) _classes.get( clss );
		if ( w != null )
			throw new TypeException( "class overloaded: " + clss.getName() );

		_classes.put( clss, def );
	}
	

    public Set getNames()
    {
    	return _names.keySet();
    }
}
