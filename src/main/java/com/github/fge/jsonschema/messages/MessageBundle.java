package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;

import java.util.ResourceBundle;

public final class MessageBundle
{
    private final ResourceBundle bundle;
    private final ErrorProvider errorProvider;

    public MessageBundle(final String name, final ErrorProvider errorProvider)
    {
        bundle = ResourceBundle.getBundle(name);
        this.errorProvider = errorProvider;
    }

    public String getString(final String key)
    {
        return bundle.getString(key);
    }

    public void checkNotNull(final Object obj, final String key)
    {
        if (obj == null)
            throw errorProvider.doError(bundle.getString(key));
    }

    interface ErrorProvider
    {
        ProcessingError doError(final String msg);
    }
}
