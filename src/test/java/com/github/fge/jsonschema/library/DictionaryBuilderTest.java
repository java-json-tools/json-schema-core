/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.CoreMessageBundle;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class DictionaryBuilderTest
{
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

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
            assertEquals(e.getMessage(), BUNDLE.getKey("dictionary.nullKey"));
        }
    }

    @Test
    public void cannotInsertNullValue()
    {
        try {
            builder.addEntry(KEY, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("dictionary.nullValue"));
        }
    }

    @Test
    public void cannotImportFromNullDictionary()
    {
        try {
            builder.addAll(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("dictionary.nullDict"));
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
