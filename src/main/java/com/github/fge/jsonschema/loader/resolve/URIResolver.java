package com.github.fge.jsonschema.loader.resolve;

import com.google.common.annotations.Beta;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

@Beta
public interface URIResolver
{
    InputStream resolve(final URI uri)
        throws IOException;
}
