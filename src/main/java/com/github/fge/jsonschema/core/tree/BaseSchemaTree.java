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
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.TokenResolver;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.concurrent.Immutable;

/**
 * Base implementation of a {@link SchemaTree}
 *
 * @see CanonicalSchemaTree
 * @see InlineSchemaTree
 */
@Immutable
@ParametersAreNonnullByDefault
public abstract class BaseSchemaTree
    implements SchemaTree
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    protected final SchemaKey key;

    /**
     * The contents of {@code $schema} for that schema
     *
     * <p>Note that it is required that if it is present, it be an absolute
     * JSON Reference. If no suitable {@code $schema} is found, an empty ref
     * is returned.</p>
     */
    private final JsonRef dollarSchema;

    /**
     * The initial node
     */
    protected final JsonNode baseNode;

    /**
     * The current JSON Pointer into the node. Starts empty.
     */
    protected final JsonPointer pointer;

    /**
     * The current node.
     */
    private final JsonNode node;

    /**
     * The JSON Reference representing the context at the root of the schema
     *
     * <p>If there is an {@code id} at the top level, this will take precedence
     * over {@link SchemaKey#getLoadingRef()} (provided the id is well
     * formed).</p>
     */
    private final JsonRef startingRef;

    /**
     * The current resolution context
     */
    private final JsonRef currentRef;

    protected BaseSchemaTree(final SchemaKey key, final JsonNode baseNode,
        final JsonPointer pointer)
    {
        Preconditions.checkNotNull(key);
        Preconditions.checkNotNull(baseNode);
        Preconditions.checkNotNull(pointer);

        this.key = key;
        dollarSchema = extractDollarSchema(baseNode);

        this.baseNode = baseNode;
        this.pointer = pointer;
        node = pointer.path(baseNode);

        final JsonRef loadingRef = key.getLoadingRef();
        final JsonRef ref = idFromNode(baseNode);

        startingRef = ref == null ? loadingRef : loadingRef.resolve(ref);
        currentRef = nextRef(startingRef, pointer, baseNode);
    }

    @Deprecated
    protected BaseSchemaTree(final JsonRef loadingRef, final JsonNode baseNode,
        final JsonPointer pointer)
    {
        dollarSchema = extractDollarSchema(baseNode);

        key = loadingRef.equals(JsonRef.emptyRef()) ? SchemaKey.anonymousKey()
            : SchemaKey.forJsonRef(loadingRef);

        this.baseNode = baseNode;
        this.pointer = pointer;
        node = pointer.path(baseNode);

        final JsonRef ref = idFromNode(baseNode);

        startingRef = ref == null ? loadingRef : loadingRef.resolve(ref);

        currentRef = nextRef(startingRef, pointer, baseNode);
    }

    protected BaseSchemaTree(final BaseSchemaTree other,
        final JsonPointer newPointer)
    {
        key = other.key;

        dollarSchema = other.dollarSchema;
        baseNode = other.baseNode;

        pointer = newPointer;
        node = newPointer.path(baseNode);

        startingRef = other.startingRef;
        currentRef = nextRef(startingRef, newPointer, baseNode);
    }

    @Override
    @Deprecated
    public final long getId()
    {
        return key.getId();
    }

    @Override
    public final JsonNode getBaseNode()
    {
        return baseNode;
    }

    @Override
    public final JsonPointer getPointer()
    {
        return pointer;
    }

    @Override
    public final JsonNode getNode()
    {
        return node;
    }

    /**
     * Resolve a JSON Reference against the current resolution context
     *
     * @param other the JSON Reference to resolve
     * @return the resolved reference
     * @see JsonRef#resolve(JsonRef)
     */
    @Override
    public final JsonRef resolve(final JsonRef other)
    {
        return currentRef.resolve(other);
    }

    @Override
    public final JsonRef getDollarSchema()
    {
        return dollarSchema;
    }

    /**
     * Get the loading URI for that schema
     *
     * @return the loading URI as a {@link JsonRef}
     */
    @Override
    public final JsonRef getLoadingRef()
    {
        return key.getLoadingRef();
    }

    /**
     * Get the current resolution context
     *
     * @return the context as a {@link JsonRef}
     */
    @Override
    public final JsonRef getContext()
    {
        return currentRef;
    }

    @Override
    public final JsonNode asJson()
    {
        final ObjectNode ret = FACTORY.objectNode();

        ret.put("loadingURI", FACTORY.textNode(key.getLoadingRef().toString()));
        ret.put("pointer", FACTORY.textNode(pointer.toString()));

        return ret;
    }

    @Nonnull
    @Override
    public final String toString()
    {
        return new StringBuilder()
            .append(this.getClass().getSimpleName()).append("{")
            .append("key").append("=").append(key)
            .append(", pointer").append("=").append(pointer)
            .append(", URI context").append("=").append(currentRef)
            .append("}")
            .toString();
    }

    @Override
    public final int hashCode()
    {
        return key.hashCode() ^ pointer.hashCode();
    }

    @Override
    public final boolean equals(@Nullable final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final BaseSchemaTree other = (BaseSchemaTree) obj;
        return key.equals(other.key) && pointer.equals(other.pointer);
    }

    /**
     * Build a JSON Reference from a node
     *
     * <p>This will return {@code null} if the reference could not be built. The
     * conditions for a successful build are as follows:</p>
     *
     * <ul>
     *     <li>the node is an object;</li>
     *     <li>it has a member named {@code id};</li>
     *     <li>the value of this member is a string;</li>
     *     <li>this string is a valid URI.</li>
     * </ul>
     *
     * @param node the node
     * @return a JSON Reference, or {@code null}
     */
    @Nullable
    protected static JsonRef idFromNode(final JsonNode node)
    {
        if (!node.path("id").isTextual())
            return null;

        try {
            return JsonRef.fromString(node.get("id").textValue());
        } catch (JsonReferenceException ignored) {
            return null;
        }
    }

    /**
     * Calculate the next URI context from a starting reference and node
     *
     * @param startingRef the starting reference
     * @param ptr the JSON Pointer
     * @param startingNode the starting node
     * @return the calculated reference
     */
    private static JsonRef nextRef(final JsonRef startingRef,
        final JsonPointer ptr, final JsonNode startingNode)
    {
        JsonRef ret = startingRef;
        JsonRef idRef;
        JsonNode node = startingNode;

        for (final TokenResolver<JsonNode> resolver: ptr) {
            node = resolver.get(node);
            if (node == null)
                break;
            idRef = idFromNode(node);
            if (idRef != null)
                ret = ret.resolve(idRef);
        }

        return ret;
    }

    private static JsonRef extractDollarSchema(final JsonNode schema)
    {
        final JsonNode node = schema.path("$schema");

        if (!node.isTextual())
            return JsonRef.emptyRef();

        try {
            final JsonRef ref = JsonRef.fromString(node.textValue());
            return ref.isAbsolute() ? ref : JsonRef.emptyRef();
        } catch (JsonReferenceException ignored) {
            return JsonRef.emptyRef();
        }
    }
}
