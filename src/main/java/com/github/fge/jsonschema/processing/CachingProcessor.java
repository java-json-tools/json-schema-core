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
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.core.util.equivalence.Equivalences;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

/**
 * A class caching the result of a {@link Processor}
 *
 * <p>You can use this over whichever processor of your choice. Internally, it
 * uses a {@link LoadingCache} to store results.</p>
 *
 * <p>You can optionally pass an {@link Equivalence} as an argument for cache
 * keys. By default, {@link Equivalences#equals()} will be used.</p>
 *
 * @param <IN> input type for that processor
 * @param <OUT> output type for that processor
 */
public final class CachingProcessor<IN extends MessageProvider, OUT extends MessageProvider>
    implements Processor<IN, OUT>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * The wrapped processor
     */
    private final Processor<IN, OUT> processor;

    /**
     * The equivalence to use
     */
    private final Equivalence<IN> equivalence;

    /**
     * The cache
     */
    private final LoadingCache<Equivalence.Wrapper<IN>, ProcessingResult<OUT>>
        cache;

    /**
     * Constructor
     *
     * <p>This is equivalent to calling {@link #CachingProcessor(Processor,
     * Equivalence)} with {@link Equivalences#equals()} as the second argument.
     * </p>
     *
     * @param processor the processor
     */
    public CachingProcessor(final Processor<IN, OUT> processor)
    {
        this(processor, Equivalences.<IN>equals());
    }

    /**
     * Main constructor
     *
     * @param processor the processor
     * @param equivalence an equivalence to use for cache keys
     * @throws NullPointerException processor or equivalence are null
     */
    public CachingProcessor(final Processor<IN, OUT> processor,
        final Equivalence<IN> equivalence)
    {
        BUNDLE.checkNotNull(processor, "processing.nullProcessor");
        BUNDLE.checkNotNull(equivalence, "processing.nullEquivalence");
        this.processor = processor;
        this.equivalence = equivalence;
        cache = CacheBuilder.newBuilder().build(loader());
    }

    @Override
    public OUT process(final ProcessingReport report, final IN input)
        throws ProcessingException
    {
        final ProcessingResult<OUT> result;
        try {
            result = cache.get(equivalence.wrap(input));
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
        report.mergeWith(result.getReport());
        return result.getResult();
    }

    private CacheLoader<Equivalence.Wrapper<IN>, ProcessingResult<OUT>> loader()
    {
        return new CacheLoader<Equivalence.Wrapper<IN>, ProcessingResult<OUT>>()
        {
            @Override
            public ProcessingResult<OUT> load(final Equivalence.Wrapper<IN> key)
                throws ProcessingException
            {
                final IN input = key.get();
                final ListProcessingReport report
                    = new ListProcessingReport(LogLevel.DEBUG, LogLevel.NONE);
                return ProcessingResult.of(processor, report, input);
            }
        };
    }

    @Override
    public String toString()
    {
        return "CACHED[" + processor + ']';
    }
}
