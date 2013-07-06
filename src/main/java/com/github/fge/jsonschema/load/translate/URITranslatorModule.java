package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

import java.net.URI;
import java.util.Map;

public class URITranslatorModule
    extends AbstractModule
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private URI namespace = URI.create("");
    private final PathRedirectRegistry pathRedirectRegistry
        = new PathRedirectRegistry();
    private final SchemaRedirectRegistry schemaRedirectRegistry
        = new SchemaRedirectRegistry();

    protected final void setNamespace(final URI namespace)
    {
        BUNDLE.checkNotNull(namespace, "uriTransform.nullInput");
        URIUtils.checkPathURI(namespace);
        this.namespace = namespace;
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
        bind(URI.class).toInstance(namespace);
        bind(new TypeLiteral<Map<URI, URI>>() {})
            .annotatedWith(Names.named("pathRedirects"))
            .toInstance(pathRedirectRegistry.build());
        bind(new TypeLiteral<Map<URI, URI>>() {})
            .annotatedWith(Names.named("schemaRedirects"))
            .toInstance(schemaRedirectRegistry.build());
    }
}
