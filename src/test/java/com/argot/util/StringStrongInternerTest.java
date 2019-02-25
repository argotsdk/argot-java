package com.argot.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.nio.CharBuffer;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

public class StringStrongInternerTest {
    private StringStrongInterner interner;

    @Before
    public void setup() {
        this.interner = StringStrongInterner.getInterner("test");
    }

    @Test
    public void testEmpty() {
        final String str = "";
        String result = interner.get(CharBuffer.wrap(str));
        assertNotNull(result);
        assertEquals(result, "");

        result = interner.get(CharBuffer.wrap(str));
        assertNotNull(result);
        assertEquals(result, "");
    }

    @Test
    public void testInSameEmpty() {
        String str = "This is a test";
        CharBuffer buffer = CharBuffer.wrap(str);

        String result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);

        str = "Zrdvarksab";
        buffer = CharBuffer.wrap(str);

        result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);

        result = interner.get(buffer);
        assertNotNull(result);
        assertEquals(result, str);
    }

    @Test
    public void testMultiple() {

        for (int x = 0; x < 1000000; x++) {
            String str = randomString();
            CharBuffer buffer = CharBuffer.wrap(str);

            // adds it.
            String result = interner.get(buffer);
            assertNotNull(result);
            assertEquals(result, str);

            // retrieves it.
            result = interner.get(buffer);
            assertNotNull(result);
            assertEquals(result, str);
        }
    }

    private Random random = new Random();

    private String randomString() {
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;

        StringBuilder buffer = new StringBuilder(targetStringLength);
        for (int i = 0; i < targetStringLength; i++) {
            int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit - leftLimit + 1));
            buffer.append((char) randomLimitedInt);
        }
        return buffer.toString();
    }

}
