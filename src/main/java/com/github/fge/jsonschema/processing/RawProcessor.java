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
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.ValueHolder;

public abstract class RawProcessor<IN, OUT>
    implements Processor<ValueHolder<IN>, ValueHolder<OUT>>
{
    private final String inputName;
    private final String outputName;

    protected RawProcessor(final String inputName, final String outputName)
    {
        this.inputName = inputName;
        this.outputName = outputName;
    }

    protected abstract OUT rawProcess(final ProcessingReport report,
        final IN input);

    @Override
    public final ValueHolder<OUT> process(final ProcessingReport report,
        final ValueHolder<IN> input)
        throws ProcessingException
    {
        final IN rawInput = input.getValue();
        final OUT rawOutput = rawProcess(report, rawInput);
        return ValueHolder.hold(outputName, rawOutput);
    }

    protected final ProcessingMessage newMessage(final IN rawInput)
    {
        return new ProcessingMessage().put(inputName, rawInput);
    }
}
