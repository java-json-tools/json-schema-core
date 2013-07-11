package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.annotations.Beta;
import com.google.common.base.Optional;

import javax.inject.Inject;
import java.net.URI;
import java.util.Map;

@Beta
public final class SchemaSelector
{
    private final Map<URI, SchemaDescriptor> descriptors;
    private final SchemaDescriptor defaultDescriptor;

    @Inject
    SchemaSelector(final Map<URI, SchemaDescriptor> descriptors,
        final SchemaDescriptor defaultDescriptor)
    {
        this.descriptors = descriptors;
        this.defaultDescriptor = defaultDescriptor;
    }

    public SchemaDescriptor selectDescriptor(final SchemaTree schemaTree)
    {
        final JsonNode node = schemaTree.getBaseNode().path("$schema");
        if (!node.isTextual())
            return defaultDescriptor;
        final JsonRef ref;
        try {
            ref = JsonRef.fromString(node.textValue());
        } catch (JsonReferenceException ignored) {
            return defaultDescriptor;
        }

        return Optional.fromNullable(descriptors.get(ref.getLocator()))
            .or(defaultDescriptor);
    }
}
