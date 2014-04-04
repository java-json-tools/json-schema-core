/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.processing;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.MessageProvider;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Predicate;
import com.google.common.collect.Maps;

import java.util.Map;

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
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * The predicate
     */
    private final Predicate<IN> predicate;

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
     * @throws NullPointerException the processor is null
     */
    public ProcessorSelector<IN, OUT> then(final Processor<IN, OUT> processor)
    {
        BUNDLE.checkNotNull(processor, "processing.nullProcessor");
        choices.put(predicate, processor);
        return new ProcessorSelector<IN, OUT>(this);
    }
}
