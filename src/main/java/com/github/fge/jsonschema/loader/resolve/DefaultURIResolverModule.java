package com.github.fge.jsonschema.loader.resolve;

import com.google.common.annotations.Beta;

@Beta
public class DefaultURIResolverModule
    extends URIResolverModule
{
    private final URIDownloadersRegistry downloaders
        = new URIDownloadersRegistry();

    @Override
    public final URIResolver newResolver()
    {
        return new DefaultURIResolver(downloaders);
    }

    protected final void addDownloader(final String scheme,
        final URIDownloader downloader)
    {
        downloaders.put(scheme, downloader);
    }

    protected final void removeDownloader(final String scheme)
    {
        downloaders.remove(scheme);
    }

    protected final void clearDownloaders()
    {
        downloaders.clear();
    }
}
