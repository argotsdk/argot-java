package com.argot.util;

import java.lang.management.ManagementFactory;
import java.lang.ref.WeakReference;
import java.nio.CharBuffer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class StringWeakInterner implements StringWeakInternerMBean
{
	private static StringWeakInterner interner = new StringWeakInterner();

	public static StringWeakInterner get()
	{
		return interner;
	}
	private IntObjectHashMap<StringEntry> stringMap = new IntObjectHashMap<StringEntry>();
	private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
	private final Lock r = rwl.readLock();
	private final Lock w = rwl.writeLock();

	private StringWeakInterner()
	{
		try
		{
			final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
			final ObjectName mbName = new ObjectName("com.argot:type=StringWeakInterner,Name=common");
			mbs.registerMBean(this, mbName);
		}
		catch (final MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e)
		{
			System.out.println("Argot failed to register MBean u8utf8");
			e.printStackTrace();
		}
	}

	private int hash(final CharBuffer cb)
	{
		int h = 0;
		final int i = cb.limit();
		for (int p = cb.position(); p < i; p++)
		{

			h = 31 * h + cb.get(p);
		}

		return h;
	}

	private boolean compare(final CharBuffer cb, final String str)
	{

		if (str == null)
		{
			return false;
		}

		final int i = cb.limit();
		for (int p = cb.position(); p < i; p++)
		{
			if (cb.get(p) != str.charAt(p))
			{
				return false;
			}
		}
		return true;

	}

	public String get(final CharBuffer buffer)
	{
		// get the hash and divide by eight to reduce the hash
		final int hash = (hash(buffer) >> 3);

		String string = null;

		r.lock();

		// First find the correct UUIDEntry for this hash.
		StringEntry stringEntry = stringMap.get(hash);
		if (stringEntry == null)
		{
			string = add(hash, buffer);
		}
		else
		{
			// Look through the list to find the right one.
			string = stringEntry.string.get();

			while (!compare(buffer, string))
			{
				stringEntry = stringEntry.next;

				// End of the list so not there.
				if (stringEntry == null)
				{
					string = add(hash, buffer);
					break;
				}

				string = stringEntry.string.get();
			}

		}
		r.unlock();
		return string;
	}

	/*
	 * Doesn't return the number of objects, just the number of unique hashes. Indicative of size, but not accurate.
	 */
	public int size()
	{
		return stringMap.size();
	}

	/**
	 * Just required for testing purposes.
	 */
	public void reset()
	{
		stringMap = new IntObjectHashMap<StringEntry>();
	}

	private String add(final int hash, final CharBuffer buffer)
	{
		String string = null;

		r.unlock();
		w.lock();
		// try again just in case we got blocked while another thread created the same item.
		StringEntry stringEntry = stringMap.get(hash);

		// Nothing for this hash yet.
		if (stringEntry == null)
		{

			string = buffer.toString();
			stringEntry = new StringEntry();
			stringEntry.string = new WeakReference<String>(string);
			stringMap.put(hash, stringEntry);
		}
		else
		{
			string = stringEntry.string.get();
			StringEntry reuseEntry = null;
			while (!compare(buffer, string))
			{
				final StringEntry lastEntry = stringEntry;

				// This entry in the list can be re-used. The weakReference has lost its string.
				if (string == null)
				{
					reuseEntry = stringEntry;
				}

				// go to next one in the list.
				stringEntry = stringEntry.next;

				// End of the list so not there.
				if (stringEntry == null)
				{
					string = buffer.toString();

					if (reuseEntry != null)
					{
						stringEntry = reuseEntry;
						stringEntry.string = new WeakReference<String>(string);

						// no need to link entry, it is already linked.
					}
					else
					{
						stringEntry = new StringEntry();
						stringEntry.string = new WeakReference<String>(string);

						// link the last valid entry to the new entry.
						lastEntry.next = stringEntry;
					}

					break;
				}

				string = stringEntry.string.get();
			}

		}

		r.lock();
		w.unlock();

		return string;
	}

	private static class StringEntry
	{
		StringEntry next;
		WeakReference<String> string;
	}

	@Override
	public int getSize()
	{
		return stringMap.size();
	}

	@Override
	public int getLength()
	{
		return stringMap.length();
	}
}
