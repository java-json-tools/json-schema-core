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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessorBuildError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheLoader;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.ProcessingErrors.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessingCacheTest
{
    private CacheLoader<Equivalence.Wrapper<String>, Integer> loader;
    private ProcessingCache<String, Integer> processingCache;

    @BeforeMethod
    public void init()
    {
        loader = spy(new TestLoader());
        processingCache = new ProcessingCache<String, Integer>(EQUIVALENCE,
            loader);
    }

    @Test
    public void buildingYellsIfNullEquivalence()
    {
        try {
            new ProcessingCache<Object, Object>(null, null);
            fail("No exception thrown!!");
        } catch (ProcessorBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_EQUIVALENCE);
        }
    }

    @Test
    public void buildingYellsIfLoaderIsNull()
    {
        try {
            new ProcessingCache<Object, Object>(Equivalence.equals(), null);
            fail("No exception thrown!!");
        } catch (ProcessorBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_LOADER);
        }
    }

    @Test
    public void cannotGetNullKey()
        throws ProcessingException
    {
        try {
            processingCache.get(null);
            fail("No exception thrown!!");
        } catch (ProcessingError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_KEYS_FORBIDDEN);
        }
    }

    @Test
    public void cannotGetNullKeyEvenUnchecked()
    {
        try {
            processingCache.getUnchecked(null);
            fail("No exception thrown!!");
        } catch (ProcessingError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_KEYS_FORBIDDEN);
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    public void equivalentKeysAreComputedOnlyOnce()
        throws Exception
    {
        processingCache.getUnchecked("foo");
        processingCache.getUnchecked("bar");
        processingCache.getUnchecked("baz");

        verify(loader, onlyOnce()).load(any(Equivalence.Wrapper.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void differentKeysAreComputedEachTime()
        throws Exception
    {
        processingCache.getUnchecked("hello");
        processingCache.getUnchecked("world!");

        verify(loader, times(2)).load(any(Equivalence.Wrapper.class));
    }

    private static final Equivalence<String> EQUIVALENCE
        = new Equivalence<String>()
    {
        @Override
        protected boolean doEquivalent(final String a, final String b)
        {
            return a.length() == b.length();
        }

        @Override
        protected int doHash(final String t)
        {
            return t.length();
        }
    };

    private static class TestLoader
        extends CacheLoader<Equivalence.Wrapper<String>, Integer>
    {
        @Override
        public Integer load(final Equivalence.Wrapper<String> key)
            throws Exception
        {
            return key.get().length();
        }
    }
}
