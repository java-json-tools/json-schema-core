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

package com.github.fge.jsonschema.core.tree;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;

import javax.annotation.concurrent.Immutable;

/**
 * A simple {@link JsonTree}
 */
@Immutable
public final class SimpleJsonTree
    extends BaseJsonTree
{
    public SimpleJsonTree(final JsonNode baseNode)
    {
        super(baseNode);
    }

    private SimpleJsonTree(final JsonNode baseNode, final JsonPointer pointer)
    {
        super(baseNode, pointer);
    }

    @Override
    public SimpleJsonTree append(final JsonPointer pointer)
    {
        return new SimpleJsonTree(baseNode, this.pointer.append(pointer));
    }

    @Override
    public JsonNode asJson()
    {
        return FACTORY.objectNode()
            .set("pointer", FACTORY.textNode(pointer.toString()));
    }

    @Override
    public String toString()
    {
        return "current pointer: \"" + pointer + '"';
    }
}
