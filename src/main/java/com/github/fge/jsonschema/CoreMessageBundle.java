package com.github.fge.jsonschema;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.source.MessageSource;
import com.github.fge.msgsimple.source.PropertiesMessageSource;

import java.io.IOException;

public final class CoreMessageBundle
{
    private static final CoreMessageBundle INSTANCE = new CoreMessageBundle();

    private final MessageBundle bundle;

    private CoreMessageBundle()
    {
        final MessageSource source;

        try {
            source = PropertiesMessageSource.fromResource("/core.properties");
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }

        bundle = new MessageBundle.Builder().appendSource(source).build();
    }

    public static CoreMessageBundle getInstance()
    {
        return INSTANCE;
    }

    public String getKey(final String key)
    {
        return bundle.getKey(key);
    }

    public void checkNotNull(final Object obj, final String key)
    {
        if (obj == null)
            throw new NullPointerException(bundle.getKey(key));
    }
}