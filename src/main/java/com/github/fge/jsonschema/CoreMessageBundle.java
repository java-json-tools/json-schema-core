package com.github.fge.jsonschema;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;

public final class CoreMessageBundle
{
    private static final String PATH
        = "/com/github/fge/jsonschema/core/core.properties";

    private static final CoreMessageBundle INSTANCE = new CoreMessageBundle();

    private final MessageBundle bundle = PropertiesBundle.forPath(PATH);

    private CoreMessageBundle()
    {
    }

    public static CoreMessageBundle getInstance()
    {
        return INSTANCE;
    }

    public String getKey(final String key)
    {
        return bundle.getMessage(key);
    }

    public void checkNotNull(final Object obj, final String key)
    {
        if (obj == null)
            throw new NullPointerException(bundle.getMessage(key));
    }
}