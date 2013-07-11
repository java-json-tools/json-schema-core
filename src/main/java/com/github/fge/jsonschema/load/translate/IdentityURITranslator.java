package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
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
