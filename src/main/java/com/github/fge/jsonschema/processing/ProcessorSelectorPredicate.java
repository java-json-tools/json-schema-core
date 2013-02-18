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

import com.github.fge.jsonschema.exceptions.unchecked.ProcessorBuildError;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;

/**
 * The pendant of {@link ProcessorSelector}
 *
 * <p>This is the result of {@link ProcessorSelector#when(Predicate)}. Its
 * only method, {@link #then(Processor)}, returns a {@link ProcessorSelector}.
 * </p>
 *
 * @param <IN> the input type of processors
 * @param <OUT> the output type of processors
 */
public final class ProcessorSelectorPredicate<IN extends MessageProvider, OUT extends MessageProvider>
{
    /**
     * The predicate
     */
    final Predicate<IN> predicate;

    /**
     * The existing choices
     */
    final Map<Predicate<IN>, Processor<IN, OUT>> choices;

    /**
     * The default processor
     */
    final Processor<IN, OUT> byDefault;

    /**
     * Package local constructor
     *
     * @param selector a {@link ProcessorSelector}
     * @param predicate the new predicate
     *
     * @see ProcessorSelector#when(Predicate)
     */
    ProcessorSelectorPredicate(final ProcessorSelector<IN, OUT> selector,
        final Predicate<IN> predicate)
    {
        this.predicate = predicate;
        choices = Maps.newLinkedHashMap(selector.choices);
        byDefault = selector.byDefault;
    }

    /**
     * Associate a processor to a predicate
     *
     * @param processor the associated processor
     * @return a new {@link ProcessorSelector}
     * @throws ProcessorBuildError the processor is null
     */
    public ProcessorSelector<IN, OUT> then(final Processor<IN, OUT> processor)
    {
        if (processor == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_PROCESSOR));
        choices.put(predicate, processor);
        return new ProcessorSelector<IN, OUT>(this);
    }
}
