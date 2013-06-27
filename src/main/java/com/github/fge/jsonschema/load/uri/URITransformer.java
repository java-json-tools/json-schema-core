package com.github.fge.jsonschema.load.uri;

import com.github.fge.Frozen;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;

public final class URITransformer
    implements Frozen<URITransformerBuilder>
{
    final URI namespace;

    final Map<URI, URI> pathRedirects;

    final Map<URI, URI> schemaRedirects;

    public static URITransformerBuilder newBuilder()
    {
        return new URITransformerBuilder();
    }

    public static URITransformer byDefault()
    {
        return new URITransformerBuilder().freeze();
    }

    URITransformer(final URITransformerBuilder builder)
    {
        namespace = builder.namespace;
        pathRedirects = ImmutableMap.copyOf(builder.pathRedirects);
        schemaRedirects = ImmutableMap.copyOf(builder.schemaRedirects);
    }

    public URI transform(final URI source)
    {
        URI ret = namespace.resolve(source).normalize();
        URI relative;

        for (final Map.Entry<URI, URI> entry: pathRedirects.entrySet()) {
            relative = entry.getKey().relativize(ret);
            if (!relative.equals(ret))
                ret = entry.getValue().resolve(relative);
        }

        if (schemaRedirects.containsKey(ret))
            ret = schemaRedirects.get(ret);

        return ret;
    }

    @Override
    public URITransformerBuilder thaw()
    {
        return new URITransformerBuilder(this);
    }
}
