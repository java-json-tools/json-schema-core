package com.github.fge.jsonschema.registry.read;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.annotations.Beta;
import com.google.common.io.Closer;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.EnumSet;

@Beta
public final class DefaultSchemaReader
    extends SchemaReader
{
    private final Dereferencing dereferencing;
    private final ObjectReader objectReader;

    @Inject
    public DefaultSchemaReader(final Dereferencing dereferencing,
        final EnumSet<JsonParser.Feature> parserFeatures)
    {
        this.dereferencing = dereferencing;
        final ObjectMapper mapper = JacksonUtils.newMapper();
        for (final JsonParser.Feature feature: parserFeatures)
            mapper.configure(feature, true);
        objectReader = mapper.reader();
    }

    @Override
    public SchemaTree read(final JsonRef ref, final InputStream in)
        throws IOException
    {
        final Closer closer = Closer.create();
        closer.register(in);

        try {
            final JsonNode node = objectReader.readTree(in);
            return dereferencing.newTree(ref, node);
        } finally {
            closer.close();
        }
    }

    @Override
    public SchemaTree read(final JsonRef ref, final Reader reader)
        throws IOException
    {
        final Closer closer = Closer.create();
        closer.register(reader);

        try {
            final JsonNode node = objectReader.readTree(reader);
            return dereferencing.newTree(ref, node);
        } finally {
            closer.close();
        }
    }
}
