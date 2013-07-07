package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.util.URIUtils;

import java.net.URI;

enum IdentityURITranslator
    implements URITranslator
{
    INSTANCE;

    @Override
    public URI translate(final URI source)
    {
        return URIUtils.normalizeURI(source);
    }
}
