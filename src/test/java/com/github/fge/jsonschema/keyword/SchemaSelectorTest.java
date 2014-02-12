package com.github.fge.jsonschema.keyword;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
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
        final SchemaSelectorModule cfg = new SchemaSelectorModule() {
            {
                addDescriptor(DESCRIPTOR, false);
            }
        };
        final SchemaSelector selector = new SchemaSelector(cfg);
        final SchemaDescriptor descriptor = selector.selectDescriptor(tree);
        assertSame(descriptor, DESCRIPTOR);
    }

    @Test
    public void noDollarSchemaReturnsDefaultDescriptor()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        final SchemaTree tree = new CanonicalSchemaTree(node);
        final SchemaSelectorModule cfg = new SchemaSelectorModule() {
            {
                addDescriptor(DESCRIPTOR, true);
            }
        };
        final SchemaSelector selector = new SchemaSelector(cfg);
        final SchemaDescriptor descriptor = selector.selectDescriptor(tree);
        assertSame(descriptor, DESCRIPTOR);
    }
}
