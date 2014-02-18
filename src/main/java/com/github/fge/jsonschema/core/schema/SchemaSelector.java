/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.schema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;

@Beta
public final class SchemaSelector
{
    private final Map<URI, SchemaDescriptor> descriptors;
    private final SchemaDescriptor defaultDescriptor;

    public SchemaSelector(final SchemaSelectorConfiguration cfg)
    {
        defaultDescriptor = cfg.getDefaultDescriptor();
        descriptors = ImmutableMap.copyOf(cfg.getDescriptors());
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
