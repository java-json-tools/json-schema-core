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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.equivalence.Equivalences;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class CachingProcessorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private In input;

    private Processor<In, Out> processor;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        input = mock(In.class);
        processor = mock(Processor.class);
    }

    @Test
    public void cannotInputNullProcessor()
    {
        try {
            new CachingProcessor<In, Out>(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void cannotInputNullEquivalence()
    {
        try {
            new CachingProcessor<In, Out>(processor, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullEquivalence"));
        }
    }

    @Test
    public void cachedValueIsNotProcessedTwiceButReportedTwice()
        throws ProcessingException
    {
        final Processor<In, Out> p = new CachingProcessor<In, Out>(processor,
            Equivalences.<In>identity());

        final ProcessingReport report = mock(ProcessingReport.class);

        p.process(report, input);
        p.process(report, input);

        verify(processor, only()).process(anyReport(), same(input));
        verify(report, times(2)).mergeWith(anyReport());
    }

    @Test
    public void exceptionIsThrownCorrectly()
        throws ProcessingException
    {
        final Processor<In, Out> p = new CachingProcessor<In, Out>(processor,
            Equivalences.<In>identity());
        final ProcessingReport report = mock(ProcessingReport.class);
        final ProcessingException exception = new Foo();

        when(processor.process(anyReport(), any(In.class)))
            .thenThrow(exception);

        try {
            p.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            assertSame(e, exception);
        }
    }

    private static final class Foo
        extends ProcessingException
    {
    }

    private interface In
        extends MessageProvider
    {
    }

    private interface Out
        extends MessageProvider
    {
    }
}
