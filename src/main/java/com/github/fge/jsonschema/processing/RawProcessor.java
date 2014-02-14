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
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
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
