package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
final class NamespaceURITranslator
    implements URITranslator
{
    private final URI namespace;

    NamespaceURITranslator(final URI namespace)
    {
        this.namespace = namespace;
    }

    @Override
    public URI translate(final URI source)
    {
        return URIUtils.normalizeURI(namespace.resolve(source));
    }
}
