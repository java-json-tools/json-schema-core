/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.processing;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Function;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorMapTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private Processor<In, Out> processor1;
    private Processor<In, Out> processor2;
    private Processor<In, Out> byDefault;
    private Function<In, Key> fn;
    private In input;
    private ProcessingReport report;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void initProcessors()
    {
        processor1 = mock(Processor.class);
        processor2 = mock(Processor.class);
        byDefault = mock(Processor.class);
        fn = mock(Function.class);
        input = mock(In.class);
        report = mock(ProcessingReport.class);
    }

    @Test
    public void cannotInputNullKey()
    {
        try {
            new ProcessorMap<Key, In, Out>(fn).addEntry(null, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullKey"));
        }
    }

    @Test
    public void cannotInputNullProcessor()
    {
        try {
            new ProcessorMap<Key, In, Out>(fn).addEntry(Key.ONE, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void cannotInputNullDefaultProcessor()
    {
        try {
            new ProcessorMap<Key, In, Out>(fn).addEntry(Key.ONE, processor1)
                .setDefaultProcessor(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void nullFunctionRaisesBuildError()
    {
        try {
            new ProcessorMap<Key, In, Out>(null).getProcessor();
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullFunction"));
        }
    }

    @Test
    public void appropriateProcessorIsSelectedAndRun()
        throws ProcessingException
    {
        final ProcessorMap<Key, In, Out> processorMap
            = new ProcessorMap<Key, In, Out>(fn)
            .addEntry(Key.ONE, processor1).addEntry(Key.TWO, processor2)
            .setDefaultProcessor(byDefault);

        when(fn.apply(input)).thenReturn(Key.ONE);

        final Processor<In, Out> processor = processorMap.getProcessor();

        processor.process(report, input);

        verify(processor1, only()).process(report, input);
        verify(processor2, never()).process(anyReport(), any(In.class));
        verify(byDefault, never()).process(anyReport(), any(In.class));
    }

    @Test
    public void noMatchingKeyAndNoDefaultProcessorThrowsException()
    {
        final ProcessorMap<Key, In, Out> processorMap
            = new ProcessorMap<Key, In, Out>(fn)
            .addEntry(Key.ONE, processor1).addEntry(Key.TWO, processor2);

        when(fn.apply(input)).thenReturn(Key.THREE);

        final Processor<In, Out> processor = processorMap.getProcessor();

        try {
            processor.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.getMessage("processing.noProcessor"))
                .hasField("key", Key.THREE);
        }
    }

    @Test
    public void noMatchingKeyCallsDefaultProcessorWhenSet()
        throws ProcessingException
    {
        final ProcessorMap<Key, In, Out> processorMap
            = new ProcessorMap<Key, In, Out>(fn)
            .addEntry(Key.ONE, processor1).addEntry(Key.TWO, processor2)
            .setDefaultProcessor(byDefault);

        when(fn.apply(input)).thenReturn(Key.THREE);

        final Processor<In, Out> processor = processorMap.getProcessor();

        processor.process(report, input);

        verify(processor1, never()).process(anyReport(), any(In.class));
        verify(processor2, never()).process(anyReport(), any(In.class));
        verify(byDefault, only()).process(report, input);
    }

    private enum Key { ONE, TWO, THREE }

    private interface In extends MessageProvider
    {
    }

    private interface Out extends MessageProvider
    {
    }
}
