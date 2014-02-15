package com.github.fge.jsonschema.core.load.uri;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import java.net.URI;

public final class URITranslatorConfigurationBuilder
    implements Thawed<URITranslatorConfiguration>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final URI EMPTY = URI.create("");

    URI namespace = EMPTY;

    final PathRedirectRegistry pathRedirects = new PathRedirectRegistry();

    final SchemaRedirectRegistry schemaRedirects = new SchemaRedirectRegistry();

    URITranslatorConfigurationBuilder()
    {
    }

    URITranslatorConfigurationBuilder(final URITranslatorConfiguration cfg)
    {
        namespace = cfg.namespace;
        pathRedirects.putAll(cfg.pathRedirects);
        schemaRedirects.putAll(cfg.schemaRedirects);
    }

    public URITranslatorConfigurationBuilder setNamespace(final URI uri)
    {
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        final URI normalized = URIUtils.normalizeURI(uri);
        URIUtils.checkPathURI(normalized);
        namespace = normalized;
        return this;
    }

    public URITranslatorConfigurationBuilder setNamespace(final String uri)
    {
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        return setNamespace(URI.create(uri));
    }

    public URITranslatorConfigurationBuilder addSchemaRedirect(final URI from,
        final URI to)
    {
        schemaRedirects.put(from, to);
        return this;
    }

    public URITranslatorConfigurationBuilder addSchemaRedirect(
        final String from, final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        return addSchemaRedirect(URI.create(from), URI.create(to));
    }

    public URITranslatorConfigurationBuilder addPathRedirect(final URI from,
        final URI to)
    {
        pathRedirects.put(from, to);
        return this;
    }

    public URITranslatorConfigurationBuilder addPathRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        return addPathRedirect(URI.create(from), URI.create(to));
    }

    @Override
    public URITranslatorConfiguration freeze()
    {
        return new URITranslatorConfiguration(this);
    }
}
