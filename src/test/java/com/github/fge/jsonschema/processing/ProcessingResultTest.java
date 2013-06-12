package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;
import com.google.common.collect.Iterators;
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
        = MessageBundleFactory.getBundle(JsonSchemaCoreMessageBundle.class);

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
            .thenReturn(Iterators.<ProcessingMessage>emptyIterator());

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
