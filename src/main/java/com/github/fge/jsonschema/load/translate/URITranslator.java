package com.github.fge.jsonschema.load.translate;

import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
public interface URITranslator
{
    URI translate(URI source);
}
