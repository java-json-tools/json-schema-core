package com.github.fge.jsonschema.load.uri;

import com.github.fge.Thawed;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public final class URITransformerBuilder
    implements Thawed<URITransformer>
{
    private static final URI EMPTY = URI.create("");

    URI namespace = EMPTY;

    final Map<URI, URI> pathRedirects = Maps.newHashMap();

    final Map<URI, URI> schemaRedirects = Maps.newHashMap();

    URITransformerBuilder()
    {
    }

    URITransformerBuilder(final URITransformer transformer)
    {
        namespace = transformer.namespace;
        pathRedirects.putAll(transformer.pathRedirects);
        schemaRedirects.putAll(transformer.schemaRedirects);
    }

    @Override
    public URITransformer freeze()
    {
        return new URITransformer(this);
    }
}
