package com.github.fge.jsonschema.modules.resolve;

import com.google.common.annotations.Beta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Beta
public abstract class URIResolver
{
    public abstract InputStream resolve(final URI uri)
        throws IOException;
}
