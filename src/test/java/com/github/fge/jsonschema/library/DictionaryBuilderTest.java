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

import com.github.fge.jsonschema.exceptions.unchecked.DictionaryBuildError;
import com.github.fge.jsonschema.messages.CoreMessageBundles;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class DictionaryBuilderTest
{
    private static final MessageBundle BUNDLE = CoreMessageBundles.DICTIONARY;

    private static final String KEY = "key";
    private static final Whatever MOCK1 = mock(Whatever.class);
    private static final Whatever MOCK2 = mock(Whatever.class);

    private DictionaryBuilder<Whatever> builder;

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
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullKey"));
        }
    }

    @Test
    public void cannotInsertNullValue()
    {
        try {
            builder.addEntry(KEY, null);
            fail("No exception thrown!!");
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullValue"));
        }
    }

    @Test
    public void cannotImportFromNullDictionary()
    {
        try {
            builder.addAll(null);
            fail("No exception thrown!!");
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(BUNDLE.getString("nullDict"));
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

    private interface Whatever
    {
    }
}
