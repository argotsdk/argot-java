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
package com.argot.util;

import java.lang.management.ManagementFactory;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

public class StringStrongInterner implements StringStrongInternerMBean {

    public static final HashMap<String, StringStrongInterner> interners = new HashMap<String, StringStrongInterner>();

    public synchronized static StringStrongInterner getInterner(final String name) {
        StringStrongInterner interner = interners.get(name);
        if (interner == null) {
            interner = new StringStrongInterner(name);
            interners.put(name, interner);
        }
        return interner;
    }

    private IntObjectHashMap<StringEntry> stringMap = new IntObjectHashMap<StringEntry>();
    private final ReentrantReadWriteLock rwl = new ReentrantReadWriteLock();
    private final Lock r = rwl.readLock();
    private final Lock w = rwl.writeLock();

    private StringStrongInterner(final String name) {
        try {
            final MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            final ObjectName mbName = new ObjectName("com.argot:type=StringStrongInterner,Name=" + name);
            mbs.registerMBean(this, mbName);
        } catch (final MalformedObjectNameException | InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException e) {
            System.out.println("Argot failed to register MBean " + name);
            e.printStackTrace();
        }
    }

    private int hash(final CharBuffer cb) {
        int h = 0;
        final int i = cb.limit();
        for (int p = cb.position(); p < i; p++) {

            h = 31 * h + cb.get(p);
        }

        return h;
    }

    private boolean compare(final CharBuffer cb, final String str) {

        if (str == null || cb.remaining() != str.length()) {
            return false;
        }

        final int i = cb.limit();
        for (int p = cb.position(); p < i; p++) {
            if (cb.get(p) != str.charAt(p)) {
                return false;
            }
        }
        return true;

    }

    public String get(final CharBuffer buffer) {
        // get the hash and divide by eight to reduce the hash
        final int hash = (hash(buffer) >> 3);

        String string = null;

        r.lock();

        // First find the correct UUIDEntry for this hash.
        StringEntry stringEntry = stringMap.get(hash);
        if (stringEntry == null) {
            string = add(hash, buffer);
        } else {
            // Look through the list to find the right one.
            string = stringEntry.string;

            while (!compare(buffer, string)) {
                stringEntry = stringEntry.next;

                // End of the list so not there.
                if (stringEntry == null) {
                    string = add(hash, buffer);
                    break;
                }

                string = stringEntry.string;
            }

        }
        r.unlock();
        return string;
    }

    /*
     * Doesn't return the number of objects, just the number of unique hashes. Indicative of size, but not accurate.
     */
    public int size() {
        return stringMap.size();
    }

    /**
     * Just required for testing purposes.
     */
    public void reset() {
        stringMap = new IntObjectHashMap<StringEntry>();
    }

    private String add(final int hash, final CharBuffer buffer) {
        String string = null;

        r.unlock();
        w.lock();
        // try again just in case we got blocked while another thread created the same item.
        StringEntry stringEntry = stringMap.get(hash);

        // Nothing for this hash yet.
        if (stringEntry == null) {

            string = buffer.toString();
            stringEntry = new StringEntry();
            stringEntry.string = string;
            stringMap.put(hash, stringEntry);
        } else {
            string = stringEntry.string;
            StringEntry reuseEntry = null;
            while (!compare(buffer, string)) {
                final StringEntry lastEntry = stringEntry;

                // This entry in the list can be re-used. The weakReference has lost its string.
                if (string == null) {
                    reuseEntry = stringEntry;
                }

                // go to next one in the list.
                stringEntry = stringEntry.next;

                // End of the list so not there.
                if (stringEntry == null) {
                    string = buffer.toString();

                    if (reuseEntry != null) {
                        stringEntry = reuseEntry;
                        stringEntry.string = string;

                        // no need to link entry, it is already linked.
                    } else {
                        stringEntry = new StringEntry();
                        stringEntry.string = string;

                        // link the last valid entry to the new entry.
                        lastEntry.next = stringEntry;
                    }

                    break;
                }

                string = stringEntry.string;
            }

        }

        r.lock();
        w.unlock();

        return string;
    }

    private static class StringEntry {
        StringEntry next;
        String string;
    }

    @Override
    public int getSize() {
        return stringMap.size();
    }

    @Override
    public int getLength() {
        return stringMap.length();
    }
}
