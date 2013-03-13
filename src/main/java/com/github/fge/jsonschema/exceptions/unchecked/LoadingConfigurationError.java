package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Exception thrown on loading configuration errors
 */
public final class LoadingConfigurationError
    extends ProcessingError
{
    public LoadingConfigurationError(final ProcessingMessage message)
    {
        super(message);
    }
}
