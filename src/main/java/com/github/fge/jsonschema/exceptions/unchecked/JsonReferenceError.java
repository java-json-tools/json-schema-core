package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class JsonReferenceError
    extends ProcessingConfigurationError
{
    public JsonReferenceError(final ProcessingMessage message)
    {
        super(message);
    }
}
