package com.github.fge.jsonschema.jsonpointer;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingMessage;

public final class JsonPointerException
    extends ProcessingException
{
    public JsonPointerException(final ProcessingMessage message)
    {
        super(message);
    }
}
