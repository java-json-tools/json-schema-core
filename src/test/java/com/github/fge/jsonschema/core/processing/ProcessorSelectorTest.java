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
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

import static com.github.fge.jsonschema.TestUtils.*;
import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ProcessorSelectorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private Predicate<In> predicate1;
    private Processor<In, Out> processor1;

    private Predicate<In> predicate2;
    private Processor<In, Out> processor2;

    private Processor<In, Out> byDefault;

    private ProcessorSelector<In, Out> selector;
    private List<Processor<In, Out>> otherProcessors;

    private In input;
    private ProcessingReport report;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        /*
         * We want to ensure order: have two "real" mocks, and 10 dummy others.
         */
        predicate1 = mock(Predicate.class);
        processor1 = mock(Processor.class);

        predicate2 = mock(Predicate.class);
        processor2 = mock(Processor.class);

        byDefault = mock(Processor.class);

        otherProcessors = Lists.newArrayList();

        selector = new ProcessorSelector<In, Out>();

        Predicate<In> predicate;
        Processor<In, Out> processor;

        for (int i = 0; i < 5; i++) {
            predicate = mock(Predicate.class);
            when(predicate.apply(any(In.class))).thenReturn(false);
            processor = mock(Processor.class);
            otherProcessors.add(processor);
            selector = selector.when(predicate).then(processor);
        }

        selector = selector.when(predicate1).then(processor1);

        for (int i = 0; i < 5; i++) {
            predicate = mock(Predicate.class);
            when(predicate.apply(any(In.class))).thenReturn(false);
            processor = mock(Processor.class);
            otherProcessors.add(processor);
            selector = selector.when(predicate).then(processor);
        }

        selector = selector.when(predicate2).then(processor2);

        input = mock(In.class);
        when(input.newMessage()).thenReturn(new ProcessingMessage());
        report = mock(ProcessingReport.class);
    }

    @Test
    public void cannotInputNullPredicate()
    {
        try {
            new ProcessorSelector<In, Out>().when(null).then(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullPredicate"));
        }
    }

    @Test
    public void cannotInputNullProcessor()
    {
        try {
            new ProcessorSelector<In, Out>().when(predicate1).then(null);
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
            new ProcessorSelector<In, Out>().when(predicate1).then(processor1)
                .otherwise(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("processing.nullProcessor"));
        }
    }

    @Test
    public void firstComeFirstServed()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(true);
        when(predicate2.apply(input)).thenReturn(true);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verify(processor1, onlyOnce()).process(same(report), same(input));
        verifyZeroInteractions(processor2, byDefault);

        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    @Test
    public void firstSuccessfulPredicateIsExecuted()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(true);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verify(processor2, onlyOnce()).process(same(report), same(input));
        verifyZeroInteractions(processor1, byDefault);
        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    @Test
    public void noSuccessfulPredicateAndNoDefaultThrowsException()
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(false);

        final Processor<In, Out> processor = selector.getProcessor();

        try {
            processor.process(report, input);
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            verifyZeroInteractions(processor1, processor2);
            for (final Processor<In, Out> p: otherProcessors)
                verifyZeroInteractions(p);
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.getMessage("processing.noProcessor"));
        }
    }

    @Test
    public void noSuccessfulPredicateExecutesDefault()
        throws ProcessingException
    {
        when(predicate1.apply(input)).thenReturn(false);
        when(predicate2.apply(input)).thenReturn(false);

        final Processor<In, Out> processor = selector.otherwise(byDefault)
            .getProcessor();

        processor.process(report, input);

        verifyZeroInteractions(processor1, processor2);
        verify(byDefault, onlyOnce()).process(report, input);

        for (final Processor<In, Out> p: otherProcessors)
            verifyZeroInteractions(p);
    }

    private interface In extends MessageProvider
    {
    }

    private interface Out extends MessageProvider
    {
    }
}
