package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import java.net.URI;
import java.util.Map;

public class URITranslatorModule
    extends AbstractModule
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final URI EMPTY = URI.create("");

    private URI namespace = EMPTY;
    private final PathRedirectRegistry pathRedirectRegistry
        = new PathRedirectRegistry();
    private final SchemaRedirectRegistry schemaRedirectRegistry
        = new SchemaRedirectRegistry();

    protected final void setNamespace(final URI namespace)
    {
        BUNDLE.checkNotNull(namespace, "uriChecks.nullInput");
        URIUtils.checkPathURI(namespace);
        this.namespace = URIUtils.normalizeURI(namespace);
    }

    protected final void addSchemaRedirect(final URI from, final URI to)
    {
        schemaRedirectRegistry.put(from, to);
    }

    protected final void addPathRedirect(final URI from, final URI to)
    {
        pathRedirectRegistry.put(from, to);
    }

    @Override
    protected final void configure()
    {
    }

    @Provides
    public final URITranslator getTranslator()
    {
        final boolean hasNamespace = !EMPTY.equals(namespace);
        final Map<URI, URI> schemaRedirects = schemaRedirectRegistry.build();
        final Map<URI, URI> pathRedirects = pathRedirectRegistry.build();
        final boolean hasRedirects
            = !(schemaRedirects.isEmpty() && pathRedirects.isEmpty());

        // If there are any kinds of redirects, return the full stuff
        if (hasRedirects)
            return new FullURITranslator(namespace, schemaRedirects,
                pathRedirects);

        return hasNamespace ? new NamespaceURITranslator(namespace)
            : IdentityURITranslator.INSTANCE;
    }
}
