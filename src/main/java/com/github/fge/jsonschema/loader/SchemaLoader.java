package com.github.fge.jsonschema.loader;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
public interface SchemaLoader
{
    SchemaTree load(final URI uri)
        throws ProcessingException;
}
