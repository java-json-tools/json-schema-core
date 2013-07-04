package com.github.fge.jsonschema.load.transform;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;

import javax.inject.Inject;
import javax.inject.Named;
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

    @Inject
    @Beta
    URITransformer(final URI namespace,
        @Named("schemaRedirects") final Map<URI, URI> schemaRedirects,
        @Named("pathRedirects") final Map<URI, URI> pathRedirects)
    {
        this.namespace = namespace;
        this.schemaRedirects = schemaRedirects;
        this.pathRedirects = pathRedirects;
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
