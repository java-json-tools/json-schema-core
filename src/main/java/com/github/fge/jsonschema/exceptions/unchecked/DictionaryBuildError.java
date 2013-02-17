package com.github.fge.jsonschema.exceptions.unchecked;

import com.github.fge.jsonschema.report.ProcessingMessage;

public final class DictionaryBuildError
    extends ProcessingConfigurationError
{
    public DictionaryBuildError(final ProcessingMessage message)
    {
        super(message);
    }
}
