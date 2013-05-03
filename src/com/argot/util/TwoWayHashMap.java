package com.argot.util;

import java.util.Iterator;

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



public class TwoWayHashMap
{
	private TwoWayHashItem[] _forwards;
	private TwoWayHashItem[] _backwards;
	private int _mask;
	private int _table_size;
	private int _count;


	public TwoWayHashMap()
	{
		_table_size = 32;
		_forwards = new TwoWayHashItem[_table_size];
		_backwards = new TwoWayHashItem[_table_size];
		_mask = _table_size-1;
		_count = 0;
	}

	/*
	 * int hash(int key)
	 * 
	 * fast integer hash function taken from
	 * http://www.concentric.net/~Ttwang/tech/inthash.htm
	 */
	int inthash(int key)
	{
		key += (key << 12);
		key ^= (key >> 22);
		key += (key << 4);
		key ^= (key >> 9);
		key += (key << 10);
		key ^= (key >> 2);
		key += (key << 7);
		key ^= (key >> 12);
		return key;
	}

	public int size()
	{
		return _count;
	}

	public int findKey( int val )
	{
		TwoWayHashItem item = null;
		int hashvalue = inthash(val);

		for (item = _backwards[ hashvalue & _mask ]; item != null; item = item.next_b)
		{
			if ((hashvalue == item.hash_b) && (val == item.val))
			{
				return item.key;
			}
		}
		return -1;
	}
	
	public Object getObjectFromKey( int key )
	{
		TwoWayHashItem item = null;
		int hashvalue = inthash(key);

		for (item = _forwards[ hashvalue & _mask ]; item != null; item = item.next_f)
		{
			if ((hashvalue == item.hash_f) && (key == item.key))
			{
				return item.object;
			}
		}
		return null;
	}	

	public int findValue( int key )
	{
		TwoWayHashItem item = null;
		int hashvalue = inthash(key);

		for (item = _forwards[ hashvalue & _mask ]; item != null; item = item.next_f)
		{
			if ((hashvalue == item.hash_f) && (key == item.key))
			{
				return item.val;
			}
		}
		return -1;
	}

	public void add( int key, int val, Object object )
	{
		if ( object == null )
			throw new IllegalArgumentException();
		
		TwoWayHashItem item = null;
		int hash = inthash(key);
		int index = (hash & _mask);
		
		/* return false if key already there. */
		for (item = _forwards[ index ]; item != null; item = item.next_f)
		{
			if ((hash == item.hash_f) && (key == item.key))
			{
  				throw new IllegalArgumentException();
			}
		}

		TwoWayHashItem new_item = new TwoWayHashItem();			
		new_item.key = key;
		new_item.val = val;
		new_item.object = object;

		new_item.hash_f = hash;
		new_item.next_f = _forwards[ index ];
		_forwards[index] = new_item;

		/* now calculate the backwards map. */
		hash = inthash(val);
		index = (hash & _mask);

		new_item.hash_b = hash;	
		new_item.next_b = _backwards[ index ];
		_backwards[index] = new_item;

		/* set current and increment. */
		_count++;
	}

	public void remove( int key )
	{
		TwoWayHashItem ip,item;
		int index;
		int hashvalue = inthash(key);

		for (item = _forwards[ hashvalue & _mask ]; item != null; item = item.next_f)
		{
			if ((hashvalue == item.hash_f) && (key == item.key))
			{
				break;
			}
		}
		
		/* item wasn't found.  Return */
		if ( item == null ) return;

		/* remove forward reference. */
		index = item.hash_f & _mask;
		if ( _forwards[index] == item )
		{
			_forwards[index] = item.next_f;
		}
		else
		{
			for (ip = _forwards[index]; ip.next_f != item; ip = ip.next_f)
				;
			ip.next_f = item.next_f;
		}

		/* remove backward reference */
		index = item.hash_b & _mask;
		if ( _backwards[index] == item )
		{
			_backwards[index] = item.next_f;
		}
		else
		{
			for (ip = _backwards[index]; ip.next_f != item; ip = ip.next_b)
				;
			ip.next_b = item.next_b;
		}

		/* decrement and free item. */
		_count--;  	
	}

	public Iterator<Integer> iterator()
	{
		return new TwoWayHashMapIterator(this);
	}

	public class TwoWayHashMapIterator
	implements Iterator<Integer>
	{
		private TwoWayHashMap _map;
		private int _index;
		private TwoWayHashItem _current;

		public TwoWayHashMapIterator(TwoWayHashMap map)
		{
			_map = map;
			_index = -1;
			_current = null;
		}

		public void remove()
		{
			throw new RuntimeException("Not implemented");
		}
		
		public boolean hasNext()
		{
			/* check if we can find the next one quickly */
			if ( _current != null )
			{
				_current = _current.next_f;
				if  ( _current != null )
				{
					return true;
				}
			}

			/* search through the table for a valid entry */
			while ( _current == null && _index < (_map._table_size-1) )
			{
				_index++;
				_current = _map._forwards[_index];
			}
							

			/* hit the end.  No more to get */
			if ( _current == null )
			{
				return false;
			}

			return true;
		}
		
		public Integer next()
		{
			return new Integer( _current.key );
		}

	
	}	

	public class TwoWayHashItem
	{
		public TwoWayHashItem next_b;
		public TwoWayHashItem next_f;
		public int hash_b;
		public int hash_f;
		public int val;
		public int key;
		public Object object;
	}		
}