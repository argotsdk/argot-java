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
package com.argot.meta;

import java.util.Comparator;

import com.argot.TypeException;

public class MetaVersion 
implements Comparable
{
	public static final String TYPENAME = "meta.version";
	public static final String VERSION = "1.3";
	
	private short _major;
	private short _minor;
	
	public MetaVersion()
	{
		
	}

	public MetaVersion( short major, short minor )
	{
		_major = major;
		_minor = minor;
	}
	
	public void setMajor(short major) 
	{
		_major = major;
	}

	public short getMajor() 
	{
		return _major;
	}

	public void setMinor(short minor) 
	{
		_minor = minor;
	}

	public short getMinor() 
	{
		return _minor;
	}
	
	public String toString()
	{
		return _major + "." + _minor;
	}
	
	public static MetaVersion parseVersion(String version)
	throws TypeException
	{
		if ( version == null )
			throw new TypeException("Unable to parse version");
		
		int index = version.indexOf(".");
		if ( index == -1 )
			throw new TypeException("Unable to parse version");
		
		String majorStr = version.substring(0,index);
		String minorStr = version.substring(index+1);
		
		try 
		{
			short major = Short.parseShort(majorStr);
			if ( major < 0 || major > 255 )
				throw new TypeException("Major version out of range");
			
			short minor = Short.parseShort(minorStr);
			if ( minor < 0 || minor > 255 )
				throw new TypeException("Minor version out of range");
			
			return new MetaVersion(major,minor);
		} 
		catch (NumberFormatException e) 
		{
			
			throw new TypeException("Unable to parse version", e);
		}
		
		
	}
	
	public class MetaVersionComparator
	implements Comparator
	{

		public int compare(Object o1, Object o2) 
		{
			if (o1 == null && o2 == null)
			{
				return 0;
			}
			
			if (o1 == null && o2 != null)
			{
				return -1;
			}
			
			if (o1 != null && o2 == null)
			{
				return 1;
			}
			
			MetaVersion v1 = (MetaVersion) o1;
			MetaVersion v2 = (MetaVersion) o2;
			
			return v1.toString().compareTo(v2.toString());
		}
		
	}

	public int compareTo(Object o) 
	{
		if (o==null) return -1;
		
		return this.toString().compareTo(o.toString());
	}

	public int hashCode() 
	{
		return this.toString().hashCode();
	}

	public boolean equals(Object obj) 
	{
		if (obj == null)
			return false;
		
		if (!(obj instanceof MetaVersion))
		{
			if (obj instanceof String)
			{
				return this.toString().equals(obj);
			}
			return false;
		}
		return this.toString().equals(((MetaVersion)obj).toString());
	}
	
}
