package com.github.fge.jsonschema.load.transform;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.URIUtils;
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
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        final URI normalized = URIUtils.normalizeURI(uri);
        URIUtils.checkPathURI(normalized);
        namespace = normalized;
        return this;
    }

    public URITransformerBuilder setNamespace(final String uri)
    {
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        return setNamespace(URI.create(uri));
    }

    public URITransformerBuilder addSchemaRedirect(final URI from,
        final URI to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        URI key = URIUtils.normalizeURI(from);
        URIUtils.checkSchemaURI(key);
        URI value = URIUtils.normalizeURI(to);
        URIUtils.checkSchemaURI(value);
        key = JsonRef.fromURI(key).getLocator();
        value = JsonRef.fromURI(value).getLocator();
        if (!key.equals(value))
            schemaRedirects.put(key, value);
        return this;
    }

    public URITransformerBuilder addSchemaRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        final URI src = URI.create(from);
        final URI dst = URI.create(to);
        return addSchemaRedirect(src, dst);
    }

    public URITransformerBuilder addPathRedirect(final URI from,
        final URI to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        final URI key = URIUtils.normalizeURI(from);
        URIUtils.checkPathURI(key);
        final URI value = URIUtils.normalizeURI(to);
        URIUtils.checkPathURI(value);
        if (!key.equals(value))
            pathRedirects.put(key, value);
        return this;
    }

    public URITransformerBuilder addPathRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        final URI src = URI.create(from);
        final URI dst = URI.create(to);
        return addPathRedirect(src, dst);
    }

    @Override
    public URITransformer freeze()
    {
        return new URITransformer(this);
    }
}
