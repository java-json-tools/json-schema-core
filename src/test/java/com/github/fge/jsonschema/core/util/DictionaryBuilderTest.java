/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.util;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class DictionaryBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final String KEY = "key";
    private static final Object MOCK1 = mock(Object.class);
    private static final Object MOCK2 = mock(Object.class);

    private DictionaryBuilder<Object> builder;

    @BeforeMethod
    public void createBuilder()
    {
        builder = Dictionary.newBuilder();
    }

    @Test
    public void cannotInsertNullKey()
    {
        try {
            builder.addEntry(null, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("dictionary.nullKey"));
        }
    }

    @Test
    public void cannotInsertNullValue()
    {
        try {
            builder.addEntry(KEY, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("dictionary.nullValue"));
        }
    }

    @Test
    public void cannotImportFromNullDictionary()
    {
        try {
            builder.addAll(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("dictionary.nullDict"));
        }
    }

    @Test
    public void insertedValueCanBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        assertSame(builder.freeze().entries().get(KEY), MOCK1);
    }

    @Test
    public void removedValueCannotBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        builder.removeEntry(KEY);
        assertNull(builder.freeze().entries().get(KEY));
    }

    @Test
    public void valuesCanBeOverwritten()
    {
        builder.addEntry(KEY, MOCK1);
        builder.addEntry(KEY, MOCK2);
        assertSame(builder.freeze().entries().get(KEY), MOCK2);
    }
}
