package com.github.fge.jsonschema.messages;

import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.bundle.PropertiesBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleProvider;

public final class JsonSchemaSyntaxMessageBundle
    implements MessageBundleProvider
{
    @Override
    public MessageBundle getBundle()
    {
        return PropertiesBundle.forPath("com/github/fg/jsonschema/core/syntax");
    }
}
