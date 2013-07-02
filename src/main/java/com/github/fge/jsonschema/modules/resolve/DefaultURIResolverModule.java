package com.github.fge.jsonschema.modules.resolve;

import com.google.common.annotations.Beta;

@Beta
public class DefaultURIResolverModule
    extends URIResolverModule
{
    protected final URIDownloadersMapBuilder downloaders
        = new URIDownloadersMapBuilder();

    @Override
    public final URIResolver newResolver()
    {
        return new DefaultURIResolver(downloaders);
    }
}
