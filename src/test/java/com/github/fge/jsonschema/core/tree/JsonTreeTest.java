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
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class JsonTreeTest
{
    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode testNode;
    private ObjectNode childObject;

    @BeforeClass
    public void init()
    {
        childObject = factory.objectNode();
        childObject.put("a", "b");

        final ObjectNode rootNode = factory.objectNode();
        rootNode.put("object", childObject);
        testNode = rootNode;
    }

    @Test
    public void initializedNodeTreeReturnsCorrectNodeAndPointer()
    {
        final JsonTree tree = new SimpleJsonTree(testNode);
        assertSame(tree.getNode(), testNode);
        assertEquals(tree.getPointer(), JsonPointer.empty());
    }

    @Test
    public void pushdOfJsonPointerWorks()
    {
        JsonTree tree = new SimpleJsonTree(testNode);
        final JsonPointer ptr = JsonPointer.of("object", "a");
        tree = tree.append(ptr);
        assertSame(tree.getNode(), childObject.get("a"));
        assertEquals(tree.getPointer(), ptr);
    }
}
