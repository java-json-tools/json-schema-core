package com.github.fge.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;

public abstract class AsJsonValueHolder<T extends AsJson>
    extends ValueHolder<T>
{
    protected AsJsonValueHolder(final String name, final T value)
    {
        super(name, value);
    }

    @Override
    protected final JsonNode valueAsJson()
    {
        return value.asJson();
    }
}
