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
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.jsonschema.core.report.ProcessingReport;
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
