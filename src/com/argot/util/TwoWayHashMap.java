package com.argot.util;

import java.util.Iterator;

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

	public Iterator iterator()
	{
		return new TwoWayHashMapIterator(this);
	}

	public class TwoWayHashMapIterator
	implements Iterator
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
		
		public Object next()
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