package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.equivalence.Equivalences;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.TestUtils.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class CachingProcessorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundleFactory.getBundle(JsonSchemaCoreMessageBundle.class);

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
