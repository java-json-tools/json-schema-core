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
import com.github.fge.jsonschema.core.report.AbstractProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorChainTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void cannotInitiateWithNullProcessor()
    {
        try {
            ProcessorChain.startWith(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void cannotChainWithNullProcessor()
    {
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p
            = mock(Processor.class);
        try {
            ProcessorChain.startWith(p).chainWith(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void failingOnErrorExitsEarly()
        throws ProcessingException
    {
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p1
            = mock(Processor.class);
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p2
            = mock(Processor.class);

        final Processor<MessageProvider, MessageProvider> processor
            = ProcessorChain.startWith(p1).failOnError().chainWith(p2)
                .getProcessor();

        final MessageProvider input = mock(MessageProvider.class);
        final ProcessingReport report = new DummyReport(LogLevel.ERROR);

        try {
            processor.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.getMessage("processing.chainStopped"));
        }

        verify(p1).process(same(report), any(MessageProvider.class));
        verify(p2, never()).process(any(ProcessingReport.class),
            any(MessageProvider.class));
    }

    @Test
    public void noFailureDoesNotTriggerEarlyExit()
        throws ProcessingException
    {
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p1
            = mock(Processor.class);
        @SuppressWarnings("unchecked")
        final Processor<MessageProvider, MessageProvider> p2
            = mock(Processor.class);

        final Processor<MessageProvider, MessageProvider> processor
            = ProcessorChain.startWith(p1).failOnError().chainWith(p2)
                .getProcessor();

        final MessageProvider input = mock(MessageProvider.class);
        final ProcessingReport report = new DummyReport(LogLevel.DEBUG);

        processor.process(report, input);

        verify(p1).process(same(report), any(MessageProvider.class));
        verify(p2).process(same(report), any(MessageProvider.class));
    }

    private static final class DummyReport
        extends AbstractProcessingReport
    {
        private DummyReport(final LogLevel currentLevel)
            throws ProcessingException
        {
            dispatch(new ProcessingMessage().setLogLevel(currentLevel));
        }

        @Override
        public void log(final LogLevel level, final ProcessingMessage message)
        {
        }
    }
}
