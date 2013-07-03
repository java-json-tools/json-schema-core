package com.github.fge.jsonschema.load.transform;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.URIUtils;

import java.net.URI;
import java.net.URISyntaxException;
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
        pathRedirects = builder.pathRedirects.build();
        schemaRedirects = builder.schemaRedirects.build();
    }

    public URI transform(final URI source)
    {
        URI uri = URIUtils.normalizeURI(namespace.resolve(source));
        final String fragment = uri.getFragment();

        try {
            uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("How did I get there??", e);
        }

        for (final Map.Entry<URI, URI> entry: pathRedirects.entrySet()) {
            final URI relative = entry.getKey().relativize(uri);
            if (!relative.equals(uri))
                uri = entry.getValue().resolve(relative);
        }

        uri = JsonRef.fromURI(uri).getLocator();

        if (schemaRedirects.containsKey(uri))
            uri = schemaRedirects.get(uri);

        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                fragment);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("How did I get there??", e);
        }
    }

    @Override
    public URITransformerBuilder thaw()
    {
        return new URITransformerBuilder(this);
    }
}
