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
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingConfigurationError;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Function;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.ProcessingErrors.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorMapTest
{
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
        } catch (ProcessingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_KEY);
        }
    }

    @Test
    public void cannotInputNullProcessor()
    {
        try {
            new ProcessorMap<Key, In, Out>(fn).addEntry(Key.ONE, null);
            fail("No exception thrown!!");
        } catch (ProcessingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_PROCESSOR);
        }
    }

    @Test
    public void cannotInputNullDefaultProcessor()
    {
        try {
            new ProcessorMap<Key, In, Out>(fn).addEntry(Key.ONE, processor1)
                .setDefaultProcessor(null);
            fail("No exception thrown!!");
        } catch (ProcessingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_PROCESSOR);
        }
    }

    @Test
    public void nullFunctionRaisesBuildError()
    {
        try {
            new ProcessorMap<Key, In, Out>(null).getProcessor();
            fail("No exception thrown!!");
        } catch (ProcessingConfigurationError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_FUNCTION);
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
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NO_PROCESSOR)
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
