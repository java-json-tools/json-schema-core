package com.github.fge.jsonschema.load.uri;

import com.github.fge.Frozen;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;

public final class URITranslatorConfiguration
    implements Frozen<URITranslatorConfigurationBuilder>
{
    final URI namespace;
    final Map<URI, URI> pathRedirects;
    final Map<URI, URI> schemaRedirects;

    public static URITranslatorConfigurationBuilder newBuilder()
    {
        return new URITranslatorConfigurationBuilder();
    }

    public static URITranslatorConfiguration byDefault()
    {
        return newBuilder().freeze();
    }

    URITranslatorConfiguration(final URITranslatorConfigurationBuilder builder)
    {
        namespace = builder.namespace;
        pathRedirects = ImmutableMap.copyOf(builder.pathRedirects.build());
        schemaRedirects = ImmutableMap.copyOf(builder.schemaRedirects.build());
    }

    @Override
    public URITranslatorConfigurationBuilder thaw()
    {
        return new URITranslatorConfigurationBuilder(this);
    }
}
