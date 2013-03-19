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
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;

/**
 * {@link Map}-based processor selector, with an optional default processor
 *
 * <p>The processor produced by this class works as follows:</p>
 *
 * <ul>
 *     <li>a key, of type {@code K}, is computed from the processor input, of
 *     type {@code IN}, using a {@link Function};</li>
 *     <li>the processor then looks up this key in a {@link Map}, whose values
 *     are {@link Processor}s;</li>
 *     <li>if the key exists, the appropriate procesor is executed; otherwise,
 *     the default action is performed.</li>
 * </ul>
 *
 * <p>The default action depends on whether a default processor has been
 * supplied: if none exists, a {@link ProcessingException} is thrown.</p>
 *
 * <p>The {@link Function} used to extract a key from an input is the only
 * argument of the constructor. It cannot be null.</p>
 *
 * <p>Note that <b>null keys are not allowed</b>.</p>
 *
 * @param <K> the type of keys in the map
 * @param <IN> the input type of processors
 * @param <OUT> the output type of processors
 */
public final class ProcessorMap<K, IN extends MessageProvider, OUT extends MessageProvider>
{
    private final Function<IN, K> keyFunction;
    /**
     * The map of processors
     */
    private final Map<K, Processor<IN, OUT>> processors = Maps.newHashMap();

    /**
     * The default processor
     */
    private Processor<IN, OUT> defaultProcessor = null;

    /**
     * Constructor
     *
     * @param keyFunction function to extract a key from an input
     * @throws ProcessingConfigurationError key function is null
     */
    public ProcessorMap(final Function<IN, K> keyFunction)
    {
        NULL_FUNCTION.checkThat(keyFunction != null);
        this.keyFunction = keyFunction;
    }

    /**
     * Add an entry to the processor map
     *
     * @param key the key to match against
     * @param processor the processor for that key
     * @return this
     * @throws ProcessingConfigurationError either the key or the processor are null
     */
    public ProcessorMap<K, IN, OUT> addEntry(final K key,
        final Processor<IN, OUT> processor)
    {
        NULL_KEY.checkThat(key != null);
        NULL_PROCESSOR.checkThat(processor != null);
        processors.put(key, processor);
        return this;
    }

    /**
     * Set the default processor if no matching key is found
     *
     * @param defaultProcessor the default processor
     * @return this
     * @throws ProcessingConfigurationError processor is null
     */
    public ProcessorMap<K, IN, OUT> setDefaultProcessor(
        final Processor<IN, OUT> defaultProcessor)
    {
        NULL_PROCESSOR.checkThat(defaultProcessor != null);
        this.defaultProcessor = defaultProcessor;
        return this;
    }

    /**
     * Build the resulting processor from this map selector
     *
     * <p>The resulting processor is immutable: reusing a map builder after
     * getting the processor by calling this method will not alter the
     * processor you grabbed.</p>
     *
     * @return the processor for this map selector
     */
    public Processor<IN, OUT> getProcessor()
    {
        return new Mapper<K, IN, OUT>(processors, keyFunction,
            defaultProcessor);
    }

    private static final class Mapper<K, IN extends MessageProvider, OUT extends MessageProvider>
        implements Processor<IN, OUT>
    {
        private final Map<K, Processor<IN, OUT>> processors;
        private final Function<IN, K> f;
        private final Processor<IN, OUT> defaultProcessor;

        private Mapper(final Map<K, Processor<IN, OUT>> processors,
            final Function<IN, K> f, final Processor<IN, OUT> defaultProcessor)
        {
            this.processors = ImmutableMap.copyOf(processors);
            this.f = f;
            this.defaultProcessor = defaultProcessor;
        }

        @Override
        public OUT process(final ProcessingReport report, final IN input)
            throws ProcessingException
        {
            final K key = f.apply(input);
            Processor<IN, OUT> processor = processors.get(key);

            if (processor == null)
                processor = defaultProcessor;

            if (processor == null) // Not even a default processor. Ouch.
                throw new ProcessingException(NO_PROCESSOR.asMessage()
                    .put("key", key));

            return processor.process(report, input);
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("map[")
                .append(processors.size()).append(" entries with ");
            if (defaultProcessor == null)
                sb.append("no ");
            return sb.append("default processor]").toString();
        }
    }
}

