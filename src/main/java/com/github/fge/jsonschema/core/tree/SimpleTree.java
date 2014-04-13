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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.util.AsJson;

/**
 * A JSON value decorated with JSON Pointer information
 *
 * <p>This is a {@link JsonNode} with an internal path represented as a {@link
 * JsonPointer}. The current path and node are retrievable. If the current
 * pointer points to a non existent path in the document, the retrieved node is
 * a {@link MissingNode}.</p>
 *
 * @see JsonPointer
 */
public interface SimpleTree
    extends AsJson
{
    /**
     * Return the node this tree was created with
     *
     * <p>Note: in current Jackson versions, this node is unfortunately mutable,
     * so be careful...</p>
     *
     * @return the node
     */
    JsonNode getBaseNode();

    /**
     * Get the current path into the document
     *
     * @return the path as a JSON Pointer
     */
    JsonPointer getPointer();

    /**
     * Get the node at the current path
     *
     * @return the matching node (a {@link MissingNode} if there is no matching
     * node at that pointer)
     */
    JsonNode getNode();
}
