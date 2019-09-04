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
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessingResultTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final String MSG = "Houston, we have a problem";

    private Processor<In, Out> processor;
    private ProcessingReport report;
    private In input;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        processor = mock(Processor.class);
        report = mock(ProcessingReport.class);
        input = mock(In.class);
    }

    @Test
    public void cannotSubmitNullProcessor()
        throws ProcessingException
    {
        try {
            ProcessingResult.of(null, null, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void cannotSubmitNullReport()
        throws ProcessingException
    {
        try {
            ProcessingResult.of(processor, null, null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullReport"));
        }
    }

    @Test
    public void successIsCorrectlyReported()
        throws ProcessingException
    {
        when(report.isSuccess()).thenReturn(true);
        final ProcessingResult<Out> result
            = ProcessingResult.of(processor, report, null);
        assertTrue(result.isSuccess());
    }

    @Test
    public void failureIsCorrectlyReported()
        throws ProcessingException
    {
        when(report.isSuccess()).thenReturn(false);
        final ProcessingResult<Out> result
            = ProcessingResult.of(processor, report, null);
        assertFalse(result.isSuccess());
    }

    @Test
    public void actualResultIsCorrectlySet()
        throws ProcessingException
    {
        final Out output = mock(Out.class);
        when(report.isSuccess()).thenReturn(true);
        when(processor.process(report, input)).thenReturn(output);

        final ProcessingResult<Out> result
            = ProcessingResult.of(processor, report, input);

        assertSame(result.getResult(), output);
    }

    @Test
    public void exceptionsAreCorrectlyThrown()
        throws ProcessingException
    {
        final ProcessingException exception = new ProcessingException(MSG);
        when(processor.process(anyReport(), any(In.class)))
            .thenThrow(exception);

        try {
            ProcessingResult.of(processor, report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            assertSame(exception, e);
        }
    }

    @Test
    public void uncheckedReportCorrectlyReportsException()
        throws ProcessingException
    {
        final ProcessingException exception = new ProcessingException(MSG);
        when(processor.process(anyReport(), any(In.class)))
            .thenThrow(exception);
        when(report.iterator())
            .thenReturn(ImmutableSet.<ProcessingMessage>of().iterator());

        final ProcessingResult<Out> result
            = ProcessingResult.uncheckedResult(processor, report, input);

        assertFalse(result.isSuccess());

        final ProcessingReport r = result.getReport();
        final List<ProcessingMessage> list = Lists.newArrayList(r);
        assertFalse(list.isEmpty());

        final ProcessingMessage message = list.get(0);
        assertMessage(message).hasMessage(MSG).hasLevel(LogLevel.FATAL);
    }

    private interface In extends MessageProvider
    {
    }

    private interface Out extends MessageProvider
    {
    }
}
