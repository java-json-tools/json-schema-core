package com.github.fge.jsonschema.modules.read;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@Beta
public abstract class ReaderModule
    extends AbstractModule
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Override
    protected final void configure()
    {
    }

    @Provides
    public final SchemaReader getReader()
    {
        return newReader();
    }

    protected abstract SchemaReader newReader();
}
