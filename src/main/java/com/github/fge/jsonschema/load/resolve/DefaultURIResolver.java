package com.github.fge.jsonschema.load.resolve;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

@Beta
public final class DefaultURIResolver
    implements URIResolver
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final Map<String, URIDownloader> downloaders;

    public DefaultURIResolver(final URIDownloadersRegistry builder)
    {
        downloaders = builder.build();
    }

    @Override
    public InputStream resolve(final URI uri)
        throws IOException
    {
        final String scheme = uri.getScheme();
        final URIDownloader downloader = downloaders.get(scheme);
        if (downloader == null)
            throw new IOException(
                BUNDLE.printf("refProcessing.unhandledScheme", scheme, uri));
        return downloader.fetch(uri);
    }
}
