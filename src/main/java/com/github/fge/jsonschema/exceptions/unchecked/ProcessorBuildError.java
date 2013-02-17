package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class ProcessorBuildError
    extends ProcessingConfigurationError
{
    public ProcessorBuildError(final ProcessingMessage message)
    {
        super(message);
    }
}
