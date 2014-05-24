/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.InlineSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;

/**
 * Dereferencing modes
 *
 * <p>Draft v4 defines two dereferencing modes: canonical and inline. This enum
 * defines those two modes, along with methods to generate appropriate schema
 * trees.</p>
 *
 * @see InlineSchemaTree
 * @see CanonicalSchemaTree
 */
public enum Dereferencing
{
    /**
     * Canonical dereferencing
     *
     * @see CanonicalSchemaTree
     */
    CANONICAL("canonical")
    {
        @Override
        protected SchemaTree newTree(final SchemaKey key, final JsonNode node)
        {
            return new CanonicalSchemaTree(key, node);
        }
    },
    /**
     * Inline dereferencing
     *
     * @see InlineSchemaTree
     */
    INLINE("inline")
    {
        @Override
        protected SchemaTree newTree(final SchemaKey key, final JsonNode node)
        {
            return new InlineSchemaTree(key, node);
        }
    };

    private final String name;

    /**
     * Create a new schema tree with a given loading URI and JSON Schema
     *
     * @param ref the location
     * @param node the schema
     * @return a new tree
     */
    public SchemaTree newTree(final JsonRef ref, final JsonNode node)
    {
        return newTree(SchemaKey.forJsonRef(ref), node);
    }

    /**
     * Create a new schema tree with an empty loading URI
     *
     * @param node the schema
     * @return a new tree
     */
    public SchemaTree newTree(final JsonNode node)
    {
        return newTree(SchemaKey.anonymousKey(), node);
    }

    protected abstract SchemaTree newTree(SchemaKey key, JsonNode node);

    Dereferencing(final String name)
    {
        this.name = name;
    }

    @Override
    public String toString()
    {
        return name;
    }
}
