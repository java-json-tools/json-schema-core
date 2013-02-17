package com.github.fge.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;

public abstract class ValueHolder<T>
    implements MessageProvider
{
    protected final String name;
    protected final T value;

    protected ValueHolder(final String name, final T value)
    {
        this.name = name;
        this.value = value;
    }

    protected abstract JsonNode valueAsJson();

    public final T getValue()
    {
        return value;
    }

    @Override
    public final ProcessingMessage newMessage()
    {
        return new ProcessingMessage().put(name, valueAsJson());
    }
}
