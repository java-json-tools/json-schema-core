package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

import static com.github.fge.jsonschema.messages.ProcessingMessages.*;

public abstract class ProcessorMap<K, IN extends MessageProvider, OUT extends MessageProvider>
{
    protected final Map<K, Processor<IN, OUT>> processors = Maps.newHashMap();
    protected Processor<IN, OUT> defaultProcessor = null;

    public final ProcessorMap<K, IN, OUT> addEntry(final K key,
        final Processor<IN, OUT> processor)
    {
        processors.put(key, processor);
        return this;
    }

    public final ProcessorMap<K, IN, OUT> setDefaultProcessor(
        final Processor<IN, OUT> defaultProcessor)
    {
        this.defaultProcessor = defaultProcessor;
        return this;
    }

    public final Processor<IN, OUT> getProcessor()
    {
        return new Mapper<K, IN, OUT>(processors, f(), defaultProcessor);
    }

    protected abstract Function<IN, K> f();

    private static final class Mapper<K, IN extends MessageProvider, OUT extends MessageProvider>
        implements Processor<IN, OUT>
    {
        private final Map<K, Processor<IN, OUT>> processors;
        private final Function<IN, K> f;
        private final Processor<IN, OUT> defaultProcessor;

        Mapper(final Map<K, Processor<IN, OUT>> processors,
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
            if (processors.containsKey(key))
                return processors.get(key).process(report, input);

            if (defaultProcessor != null)
                return defaultProcessor.process(report, input);

            throw new ProcessingException(new ProcessingMessage()
                .message(NO_SUITABLE_PROCESSOR).put("key", key));
        }
    }
}

