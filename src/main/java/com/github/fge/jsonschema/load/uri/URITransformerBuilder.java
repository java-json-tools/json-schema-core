package com.github.fge.jsonschema.load.uri;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public final class URITransformerBuilder
    implements Thawed<URITransformer>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

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

    public URITransformerBuilder setNamespace(final URI uri)
    {
        BUNDLE.checkNotNull(uri, "uriTransform.nullInput");
        namespace = toPathURI(uri);
        return this;
    }

    public URITransformerBuilder setNamespace(final String uri)
    {
        BUNDLE.checkNotNull(uri, "uriTransform.nullInput");
        return setNamespace(URI.create(uri));
    }

    public URITransformerBuilder addSchemaRedirect(final URI from,
        final URI to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        final URI key = toSchemaURI(from);
        final URI value = toSchemaURI(to);
        if (!key.equals(value))
            schemaRedirects.put(key, value);
        return this;
    }

    public URITransformerBuilder addSchemaRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        final URI src = URI.create(from);
        final URI dst = URI.create(to);
        return addSchemaRedirect(src, dst);
    }

    public URITransformerBuilder addPathRedirect(final URI from,
        final URI to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        final URI key = toPathURI(from);
        final URI value = toPathURI(to);
        if (!key.equals(value))
            pathRedirects.put(key, value);
        return this;
    }

    public URITransformerBuilder addPathRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        final URI src = URI.create(from);
        final URI dst = URI.create(to);
        return addPathRedirect(src, dst);
    }

    @Override
    public URITransformer freeze()
    {
        return new URITransformer(this);
    }

    private static URI toSchemaURI(final URI uri)
    {
        final URI normalized = uri.normalize();
        BUNDLE.checkArgumentPrintf(normalized.isAbsolute(),
            "uriTransform.notAbsolute", uri);
        final JsonRef ref = JsonRef.fromURI(normalized);
        BUNDLE.checkArgumentPrintf(ref.isAbsolute(),
            "uriTransform.notAbsoluteRef", uri);
        BUNDLE.checkArgumentPrintf(!normalized.getPath().endsWith("/"),
            "uriTransform.endingSlash", uri);
        return ref.getLocator();
    }

    private static URI toPathURI(final URI uri)
    {
        final URI normalized = uri.normalize();
        BUNDLE.checkArgumentPrintf(normalized.isAbsolute(),
            "uriTransform.notAbsolute", uri);
        BUNDLE.checkArgumentPrintf(uri.getFragment() == null,
            "uriTransform.fragmentNotNull", uri);
        BUNDLE.checkArgumentPrintf(uri.getQuery() == null,
            "uriTransform.queryNotNull", uri);
        BUNDLE.checkArgumentPrintf(uri.getPath().endsWith("/"),
            "uriTransform.noEndingSlash", uri);
        return normalized;
    }
}
