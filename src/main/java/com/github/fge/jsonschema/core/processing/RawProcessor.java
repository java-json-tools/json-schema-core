/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.processing;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.ValueHolder;

/**
 * Processor wrapper class
 *
 * <p>This class allows to declare a {@link Processor} with "raw" types, that
 * is inputs and outputs which do not implement {@link MessageProvider}. Inputs
 * and outputs are automatically wrapped into a {@link ValueHolder}.</p>
 *
 * <p>Implementations of this class are only required to provide a name by which
 * the input and output will be identified in a processing message.</p>
 *
 * @param <IN> type of input
 * @param <OUT> type of output
 */
public abstract class RawProcessor<IN, OUT>
    implements Processor<ValueHolder<IN>, ValueHolder<OUT>>
{
    private final String inputName;
    private final String outputName;

    /**
     * Protected constructor
     *
     * @param inputName name of the input
     * @param outputName name of the output
     */
    protected RawProcessor(final String inputName, final String outputName)
    {
        this.inputName = inputName;
        this.outputName = outputName;
    }

    /**
     * Process a raw input, return a raw output
     *
     * @param report the report to use
     * @param input the raw input
     * @return the raw output
     * @throws ProcessingException processing failure
     * @see #newMessage(Object)
     */
    protected abstract OUT rawProcess(ProcessingReport report, IN input)
        throws ProcessingException;

    @Override
    public final ValueHolder<OUT> process(final ProcessingReport report,
        final ValueHolder<IN> input)
        throws ProcessingException
    {
        final IN rawInput = input.getValue();
        final OUT rawOutput = rawProcess(report, rawInput);
        return ValueHolder.hold(outputName, rawOutput);
    }

    /**
     * Create a new processing message for reporting purposes
     *
     * @param rawInput the raw input
     * @return a new message
     */
    protected final ProcessingMessage newMessage(final IN rawInput)
    {
        return new ProcessingMessage().put(inputName, rawInput);
    }
}
