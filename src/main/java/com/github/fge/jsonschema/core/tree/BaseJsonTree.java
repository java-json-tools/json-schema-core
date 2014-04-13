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
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;

/**
 * Base implementation of a {@link JsonTree}
 */
public abstract class BaseJsonTree
    implements JsonTree
{
    protected static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

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
     * Protected constructor
     *
     * <p>This is equivalent to calling {@link
     * BaseJsonTree#BaseJsonTree(JsonNode, JsonPointer)} with an empty pointer.
     * </p>
     *
     * @param baseNode the base node
     */
    protected BaseJsonTree(final JsonNode baseNode)
    {
        this(baseNode, JsonPointer.empty());
    }

    /**
     * Main constructor
     *
     * @param baseNode the base node
     * @param pointer the pointer into the base node
     */
    protected BaseJsonTree(final JsonNode baseNode, final JsonPointer pointer)
    {
        this.baseNode = baseNode;
        node = pointer.path(baseNode);
        this.pointer = pointer;
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

    @Override
    public abstract String toString();
}

