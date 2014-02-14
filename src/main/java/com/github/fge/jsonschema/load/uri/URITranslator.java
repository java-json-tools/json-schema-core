package com.github.fge.jsonschema.load.uri;

import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.core.util.URIUtils;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

public final class URITranslator
{
    private final URI namespace;
    private final Map<URI, URI> pathRedirects;
    private final Map<URI, URI> schemaRedirects;

    public URITranslator(final URITranslatorConfiguration cfg)
    {
        namespace = cfg.namespace;
        pathRedirects = ImmutableMap.copyOf(cfg.pathRedirects);
        schemaRedirects = ImmutableMap.copyOf(cfg.schemaRedirects);
    }

    public URI translate(final URI source)
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
}
