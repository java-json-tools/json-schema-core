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

package com.github.fge.jsonschema.core.keyword;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.schema.SchemaSelector;
import com.github.fge.jsonschema.core.schema.SchemaSelectorConfiguration;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.collect.ImmutableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;

import static org.testng.Assert.*;

public final class SchemaSelectorTest
{
    private static final URI LOCATOR = URI.create("foo://bar#");
    private static final SchemaDescriptor DESCRIPTOR
        = SchemaDescriptor.newBuilder().setLocator(LOCATOR).freeze();

    @DataProvider
    public Iterator<Object[]> getLocators()
    {
        return ImmutableList.of(
            new Object[] { URI.create("foo://bar") },
            new Object[] { URI.create("FOO://Bar#") },
            new Object[] { URI.create("foO://BAR") }
        ).iterator();
    }

    @Test(dataProvider = "getLocators")
    public void dollarSchemaIsNormalizedInIncomingTrees(final URI locator)
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$schema", locator.toString());
        final SchemaTree tree = new CanonicalSchemaTree(node);
        final SchemaSelectorConfiguration cfg
            = SchemaSelectorConfiguration.newBuilder()
                .addDescriptor(DESCRIPTOR, false).freeze();
        final SchemaSelector selector = new SchemaSelector(cfg);
        final SchemaDescriptor descriptor = selector.selectDescriptor(tree);
        assertSame(descriptor, DESCRIPTOR);
    }

    @Test
    public void noDollarSchemaReturnsDefaultDescriptor()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        final SchemaTree tree = new CanonicalSchemaTree(node);
        final SchemaSelectorConfiguration cfg
            = SchemaSelectorConfiguration.newBuilder()
                .addDescriptor(DESCRIPTOR, true).freeze();
        final SchemaSelector selector = new SchemaSelector(cfg);
        final SchemaDescriptor descriptor = selector.selectDescriptor(tree);
        assertSame(descriptor, DESCRIPTOR);
    }
}
