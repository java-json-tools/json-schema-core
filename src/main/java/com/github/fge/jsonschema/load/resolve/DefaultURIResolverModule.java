package com.github.fge.jsonschema.load.resolve;

import com.google.common.annotations.Beta;

@Beta
public class DefaultURIResolverModule
    extends URIResolverModule
{
    protected final URIDownloadersRegistry downloaders
        = new URIDownloadersRegistry();

    @Override
    public final URIResolver newResolver()
    {
        return new DefaultURIResolver(downloaders);
    }
}
