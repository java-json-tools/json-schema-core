package com.github.fge.jsonschema.loader.resolve;

import com.google.common.annotations.Beta;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

@Beta
public abstract class URIResolverModule
    extends AbstractModule
{
    @Provides
    public final URIResolver getResolver()
    {
        return newResolver();
    }

    @Override
    protected final void configure()
    {
    }

    protected abstract URIResolver newResolver();

}
