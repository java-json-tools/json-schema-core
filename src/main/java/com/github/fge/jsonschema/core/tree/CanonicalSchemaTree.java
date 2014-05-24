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

package com.github.fge.jsonschema.core.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * A {@link SchemaTree} using canonical dereferencing
 *
 * <p>In canonical dereferencing mode, a JSON Reference resolves within a
 * schema if and only if the URI of the document and the base URI of the
 * JSON Reference match exactly.</p>
 *
 * <p>That is, {@code x://y/z#/foo/bar} resolves within the schema at URI
 * {@code x://y/z#}, but {@code x://y/t#} does not.</p>
 */
@Immutable
@ParametersAreNonnullByDefault
public final class CanonicalSchemaTree
    extends BaseSchemaTree
{
    /**
     * Main constructor
     *
     * @param key the schema key
     * @param baseNode the base node
     */
    public CanonicalSchemaTree(final SchemaKey key, final JsonNode baseNode)
    {
        super(key, baseNode, JsonPointer.empty());
    }

    /**
     * Alternate constructor
     *
     * @param baseNode the base node
     * @deprecated use {@link #CanonicalSchemaTree(SchemaKey, JsonNode)} instead
     */
    @Deprecated
    public CanonicalSchemaTree(final JsonNode baseNode)
    {
        this(SchemaKey.anonymousKey(), baseNode);
    }

    @Deprecated
    public CanonicalSchemaTree(final JsonRef loadingRef,
        final JsonNode baseNode)
    {
        this(SchemaKey.forJsonRef(loadingRef), baseNode);
    }

    private CanonicalSchemaTree(final CanonicalSchemaTree other,
        final JsonPointer newPointer)
    {
        super(other, newPointer);
    }

    @Override
    public SchemaTree append(final JsonPointer pointer)
    {
        final JsonPointer newPointer = this.pointer.append(pointer);
        return new CanonicalSchemaTree(this, newPointer);
    }

    @Override
    public SchemaTree setPointer(final JsonPointer pointer)
    {
        return new CanonicalSchemaTree(this, pointer);
    }

    @Override
    public boolean containsRef(final JsonRef ref)
    {
        return key.getLoadingRef().contains(ref);
    }

    @Nullable
    @Override
    public JsonPointer matchingPointer(final JsonRef ref)
    {
        if (!ref.isLegal())
            return null;
        final JsonPointer ptr = ref.getPointer();
        return ptr.path(baseNode).isMissingNode() ? null : ptr;
    }
}
