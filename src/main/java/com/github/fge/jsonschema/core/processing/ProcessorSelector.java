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
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.annotation.concurrent.Immutable;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * A processor selector using predicates
 *
 * <p>This class allows you pair processors with a set of {@link Predicate}s.
 * Internally, it uses a {@link LinkedHashMap} where keys are predicates on
 * the input type {@code IN}, and values are processors to use if this
 * predicate returns {@code true}.</p>
 *
 * <p>As it is a {@link LinkedHashMap}, order matters: the first added predicate
 * will be evaluated first, etc. If no predicate evaluates to {@code true}, the
 * default action takes place. Depending on whether you have set a default
 * processor, this processor will be selected, or a {@link ProcessingException}
 * will be thrown indicating that no appropriate selector could be found for the
 * input.</p>
 *
 * <p>Sample usage:</p>
 *
 * <pre>
 *     final Processor&lt;X, Y&gt; processor
 *         = new ProcessorSelector&lt;X, Y&gt;()
 *             .when(predicate1).then(processor1)
 *             .when(predicate2).then(processor2)
 *             .otherwise(byDefault)
 *             .getProcessor();
 * </pre>
 *
 * <p>The returned processor is immutable.</p>
 *
 * @param <IN> the input type of processors
 * @param <OUT> the output type of processors
 *
 * @see ProcessorSelectorPredicate
 */
@Immutable
public final class ProcessorSelector<IN extends MessageProvider, OUT extends MessageProvider>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * Map of predicates and their associated processors
     */
    final Map<Predicate<IN>, Processor<IN, OUT>> choices;

    /**
     * The default processor, if any
     */
    final Processor<IN, OUT> byDefault;

    /**
     * Constructor
     */
    public ProcessorSelector()
    {
        choices = Maps.newLinkedHashMap();
        byDefault = null;
    }

    /**
     * Private constructor
     *
     * @param choices the list of choices
     * @param byDefault the default processor (can be null)
     */
    private ProcessorSelector(
        final Map<Predicate<IN>, Processor<IN, OUT>> choices,
        final Processor<IN, OUT> byDefault)
    {
        this.choices = Maps.newLinkedHashMap(choices);
        this.byDefault = byDefault;
    }

    /**
     * Package local constructor
     *
     * @param selector a {@link ProcessorSelectorPredicate}
     * @see ProcessorSelectorPredicate#then(Processor)
     */
    ProcessorSelector(final ProcessorSelectorPredicate<IN, OUT> selector)
    {
        this(selector.choices, selector.byDefault);
    }

    /**
     * Add a predicate
     *
     * @param predicate the predicate to add
     * @return a {@link ProcessorSelectorPredicate}
     * @throws NullPointerException the predicate is null
     */
    public ProcessorSelectorPredicate<IN, OUT> when(
        final Predicate<IN> predicate)
    {
        BUNDLE.checkNotNull(predicate, "processing.nullPredicate");
        return new ProcessorSelectorPredicate<IN, OUT>(this, predicate);
    }

    /**
     * Set a default processor
     *
     * @param byDefault the default processor
     * @return a <b>new</b> selector
     * @throws NullPointerException default processor is null
     */
    public ProcessorSelector<IN, OUT> otherwise(
        final Processor<IN, OUT> byDefault)
    {
        BUNDLE.checkNotNull(byDefault, "processing.nullProcessor");
        return new ProcessorSelector<IN, OUT>(choices, byDefault);
    }

    /**
     * Build the processor from this selector
     *
     * <p>The returned processor is immutable: reusing this selector will not
     * affect the result of this method in any way.</p>
     *
     * @return the selector
     */
    public Processor<IN, OUT> getProcessor()
    {
        return new Chooser<IN, OUT>(choices, byDefault);
    }

    private static final class Chooser<X extends MessageProvider, Y extends MessageProvider>
        implements Processor<X, Y>
    {
        private final Map<Predicate<X>, Processor<X, Y>> map;
        private final Processor<X, Y> byDefault;

        private Chooser(final Map<Predicate<X>, Processor<X, Y>> map,
            final Processor<X, Y> byDefault)
        {
            this.map = ImmutableMap.copyOf(map);
            this.byDefault = byDefault;
        }

        @Override
        public Y process(final ProcessingReport report, final X input)
            throws ProcessingException
        {
            Predicate<X> predicate;
            Processor<X, Y> processor;
            for (final Map.Entry<Predicate<X>, Processor<X, Y>> entry:
                map.entrySet()) {
                predicate = entry.getKey();
                processor = entry.getValue();
                if (predicate.apply(input))
                    return processor.process(report, input);
            }

            if (byDefault != null)
                return byDefault.process(report, input);

            throw new ProcessingException(
                BUNDLE.getMessage("processing.noProcessor"));
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("selector[")
                .append(map.size()).append(" choices with ");
            if (byDefault == null)
                sb.append("no ");
            return sb.append("default]").toString();
        }
    }
}
