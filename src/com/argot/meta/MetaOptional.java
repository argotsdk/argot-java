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
package com.argot.meta;

import java.io.IOException;

import com.argot.TypeException;
import com.argot.TypeInputStream;
import com.argot.TypeLibraryWriter;
import com.argot.TypeMap;
import com.argot.TypeOutputStream;
import com.argot.TypeReader;
import com.argot.TypeWriter;

public class MetaOptional
extends MetaBase
implements MetaExpression
{
	public static String TYPENAME = "meta.optional";

	private MetaExpression _option;

	public MetaOptional( MetaExpression option )
	{
		_option = option;
	}
	
    public String getTypeName()
    {
        return TYPENAME;
    }
    
    public MetaExpression getOptionalExpression()
    {
        return _option;
    }

    public static class MetaOptionalTypeWriter
    implements TypeLibraryWriter,TypeWriter
    {
		public void write(TypeOutputStream out, Object o )
			throws TypeException, IOException
		{
			MetaOptional to = (MetaOptional) o;
			
			out.writeObject("meta.expression", to._option );
		}

		public TypeWriter getWriter(TypeMap map) 
		throws TypeException 
		{
			return this;
		}
    }
    
    private class MetaOptionalReader
    implements TypeReader
    {
    	private TypeReader _bool;
    	private TypeReader _option;
    	
    	public MetaOptionalReader(TypeReader bool, TypeReader option)
    	{
    		_bool = bool;
    		_option = option;
    	}
    	
		public Object read(TypeInputStream in)
		throws TypeException, IOException 
		{
			Boolean id = (Boolean) _bool.read( in );
			if ( id.booleanValue() )
			    return _option.read( in );
			return null;
		}
    	
    }
    
	public TypeReader getReader(TypeMap map )
	throws TypeException
	{
		return new MetaOptionalReader(map.getReader(map.getId("bool")),_option.getReader(map));
	}
	
	private class MetaOptionalWriter
	implements TypeWriter
	{
		private TypeWriter _bool;
		private TypeWriter _option;
		
		public MetaOptionalWriter(TypeWriter bool, TypeWriter option)
		{
			_bool = bool;
			_option = option;
		}
		
		public void write(TypeOutputStream out, Object o)
		throws TypeException, IOException 
		{
			if ( o == null )
		    {
		        _bool.write( out, new Boolean( false ));
		        return;
		    }
		    
			_bool.write( out, new Boolean( true ));
			_option.write( out, o );	
		}
		
	}

	public TypeWriter getWriter(TypeMap map)
	throws TypeException
	{
	    return new MetaOptionalWriter(map.getWriter(map.getId("bool")), _option.getWriter(map));
	}


}
