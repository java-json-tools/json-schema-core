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

package com.github.fge.jsonschema.core.keyword.collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static org.testng.Assert.*;

public abstract class AbstractPointerCollectorTest
{
    private final String keyword;
    private final PointerCollector collector;
    private final JsonNode testData;

    protected AbstractPointerCollectorTest(
        final Dictionary<PointerCollector> dict, final String prefix,
        final String keyword)
        throws IOException
    {
        this.keyword = keyword;
        final String resource = "/walk/" + prefix + '/' + keyword + ".json";
        testData = JsonLoader.fromResource(resource);
        collector = dict.entries().get(keyword);
    }

    @Test
    public final void keywordIsSupported()
    {
        assertNotNull(collector, keyword + " is not supported??");
    }

    @DataProvider
    public final Iterator<Object[]> getTestData()
        throws JsonPointerException
    {
        final List<Object[]> list = Lists.newArrayList();

        JsonNode schema;
        List<JsonPointer> pointers;

        for (final JsonNode element: testData) {
            schema = element.get("schema");
            pointers = Lists.newArrayList();
            for (final JsonNode node: element.get("pointers"))
                pointers.add(new JsonPointer(node.textValue()));
            list.add(new Object[]{ schema, pointers });
        }

        return list.iterator();
    }

    @Test(dependsOnMethods = "keywordIsSupported", dataProvider = "getTestData")
    public final void pointersAreCorrectlyComputed(final JsonNode schema,
        final List<JsonPointer> pointers)
    {
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final List<JsonPointer> collected = Lists.newArrayList();

        collector.collect(collected, tree);

        assertEquals(collected, pointers,
            "pointer list differs from expectations");
    }
}

