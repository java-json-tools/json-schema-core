package com.github.fge.jsonschema.load.translate;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import java.net.URI;

public final class URITranslatorBuilder
    implements Thawed<FullURITranslator>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final URI EMPTY = URI.create("");

    URI namespace = EMPTY;

    final PathRedirectRegistry pathRedirects
        = new PathRedirectRegistry();

    final SchemaRedirectRegistry schemaRedirects
        = new SchemaRedirectRegistry();

    URITranslatorBuilder()
    {
    }

    URITranslatorBuilder(final FullURITranslator transformer)
    {
        namespace = transformer.namespace;
        pathRedirects.putAll(transformer.pathRedirects);
        schemaRedirects.putAll(transformer.schemaRedirects);
    }

    public URITranslatorBuilder setNamespace(final URI uri)
    {
        final URI normalized = URIUtils.normalizeURI(uri);
        URIUtils.checkPathURI(normalized);
        namespace = normalized;
        return this;
    }

    public URITranslatorBuilder setNamespace(final String uri)
    {
        BUNDLE.checkNotNull(uri, "uriTransform.nullInput");
        return setNamespace(URI.create(uri));
    }

    public URITranslatorBuilder addSchemaRedirect(final URI from,
        final URI to)
    {
        schemaRedirects.put(from, to);
        return this;
    }

    public URITranslatorBuilder addSchemaRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        return addSchemaRedirect(URI.create(from), URI.create(to));
    }

    public URITranslatorBuilder addPathRedirect(final URI from,
        final URI to)
    {
        pathRedirects.put(from, to);
        return this;
    }

    public URITranslatorBuilder addPathRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriTransform.nullInput");
        BUNDLE.checkNotNull(to, "uriTransform.nullInput");
        return addPathRedirect(URI.create(from), URI.create(to));
    }

    @Override
    public FullURITranslator freeze()
    {
        return new FullURITranslator(this);
    }
}
