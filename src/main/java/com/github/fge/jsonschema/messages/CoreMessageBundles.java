package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;

public final class CoreMessageBundles
{
    private static final MessageBundle.ErrorProvider DEFAULT_PROVIDER
        = new MessageBundle.ErrorProvider()
    {
        @Override
        public ProcessingError doError(final String msg)
        {
            return new ProcessingError(msg);
        }
    };

    public static final MessageBundle SCHEMA_WALKER;
    public static final MessageBundle SYNTAX;

    static {
        String name;

        name = "schemaWalker";
        SCHEMA_WALKER = new MessageBundle(name, DEFAULT_PROVIDER);

        name = "syntax";
        SYNTAX = new MessageBundle(name, DEFAULT_PROVIDER);
    }

    private CoreMessageBundles()
    {
    }
}
