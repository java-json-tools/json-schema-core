package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.exceptions.unchecked.DictionaryBuildError;
import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.exceptions.unchecked.LoadingConfigurationError;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingConfigurationError;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;

public final class MessageBundles
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

    public static final MessageBundle JSON_REF;
    public static final MessageBundle DICTIONARY;
    public static final MessageBundle LOADING_CFG;
    public static final MessageBundle PROCESSING;
    public static final MessageBundle REF_PROCESSING;
    public static final MessageBundle SCHEMA_WALKER;

    static {
        String name;
        MessageBundle.ErrorProvider provider;

        name = "jsonref";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new JsonReferenceError(msg);
            }
        };
        JSON_REF = new MessageBundle(name, provider);

        name = "dictionary";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new DictionaryBuildError(msg);
            }
        };
        DICTIONARY = new MessageBundle(name, provider);

        name = "loadingConfiguration";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new LoadingConfigurationError(msg);
            }
        };
        LOADING_CFG = new MessageBundle(name, provider);

        name = "processing";
        provider = new MessageBundle.ErrorProvider()
        {
            @Override
            public ProcessingError doError(final String msg)
            {
                return new ProcessingConfigurationError(msg);
            }
        };
        PROCESSING = new MessageBundle(name, provider);

        name = "refProcessing";
        REF_PROCESSING = new MessageBundle(name, DEFAULT_PROVIDER);

        name = "schemaWalker";
        SCHEMA_WALKER = new MessageBundle(name, DEFAULT_PROVIDER);
    }

    private MessageBundles()
    {
    }
}
