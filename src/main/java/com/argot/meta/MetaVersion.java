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
package com.argot.meta;

import java.util.Comparator;

import com.argot.TypeException;

public class MetaVersion 
implements Comparable<MetaVersion>
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
	implements Comparator<MetaVersion>
	{

		public int compare(MetaVersion o1, MetaVersion o2) 
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

	public int compareTo(MetaVersion o) 
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
