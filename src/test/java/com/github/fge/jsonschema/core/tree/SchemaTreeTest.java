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
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.JsonPointerException;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.key.SchemaKey;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.util.Iterator;
import java.util.Set;

import static org.testng.Assert.*;

public final class SchemaTreeTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final JsonNodeFactory factory = JacksonUtils.nodeFactory();
    private JsonNode data;
    private JsonNode schema;

    @BeforeClass
    public void init()
        throws IOException
    {
        data = JsonLoader.fromResource("/tree/context.json");
        schema = data.get("schema");
    }

    @Test
    public void loadingRefIsReturnedWhenNoIdAtTopLevel()
    {
        SchemaTree schemaTree;

        final ObjectNode node = FACTORY.objectNode();

        schemaTree = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);
        assertSame(schemaTree.getContext(), JsonRef.emptyRef());

        final URI uri = URI.create("foo://bar");
        final JsonRef ref = JsonRef.fromURI(uri);

        schemaTree = new CanonicalSchemaTree(SchemaKey.forJsonRef(ref), node);
        assertSame(schemaTree.getContext(), ref);
        assertEquals(schemaTree.toString(), "CanonicalSchemaTree{key=loaded from JSON ref foo://bar#, pointer=, URI context=foo://bar#}");
    }

    @DataProvider
    public Iterator<Object[]> sampleIds()
    {
        return ImmutableSet.of(
            new Object[] { "", "http://foo.bar" },
            new Object[] { "http://foo.bar/baz", "meh#la" },
            new Object[] { "ftp://ftp.lip6.fr/schema", "x://y" }
        ).iterator();
    }

    @Test(dataProvider = "sampleIds")
    public void topMostIdIsResolvedAgainstLoadingRef(final String loading,
        final String id)
        throws ProcessingException
    {
        final JsonRef loadingRef = JsonRef.fromString(loading);
        final JsonRef idRef = JsonRef.fromString(id);
        final JsonRef resolved = loadingRef.resolve(idRef);

        final ObjectNode node = factory.objectNode();
        node.put("id", id);

        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.forJsonRef(loadingRef), node);
        assertEquals(tree.getContext(), resolved);
    }

    @DataProvider
    public Iterator<Object[]> getContexts()
    {
        final JsonNode node = data.get("lookups");

        final Set<Object[]> set = Sets.newHashSet();

        for (final JsonNode element: node)
            set.add(new Object[] {
                element.get("pointer").textValue(),
                element.get("scope").textValue()
            });

        return set.iterator();
    }

    @Test(dataProvider = "getContexts")
    public void pointerAppendCorrectlyCalculatesContext(final String path,
        final String s)
        throws JsonPointerException, JsonReferenceException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);

        assertEquals(tree.append(ptr).getContext(), scope);
    }

    @Test(dataProvider = "getContexts")
    public void pointerSetCorrectlyCalculatesContext(final String path,
        final String s)
        throws JsonPointerException, JsonReferenceException
    {
        final JsonPointer ptr = new JsonPointer(path);
        final JsonRef scope = JsonRef.fromString(s);
        SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), schema);
        final JsonRef origRef = tree.getContext();

        tree = tree.setPointer(ptr);
        assertEquals(tree.getContext(), scope);
        tree = tree.setPointer(JsonPointer.empty());
        assertEquals(tree.getContext(), origRef);
    }

    @DataProvider
    public Iterator<Object[]> nonSchemas()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.OBJECT);
    }

    @Test(dataProvider = "nonSchemas")
    public void nonSchemasYieldAnEmptyRef(final JsonNode node)
    {
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> nonStringDollarSchemas()
    {
        return SampleNodeProvider.getSamples(NodeType.STRING);
    }

    @Test
    public void schemaWithoutDollarSchemaYieldsAnEmptyRef()
    {
        final ObjectNode node = FACTORY.objectNode();
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @Test(dataProvider = "nonStringDollarSchemas")
    public void nonTextualDollarSchemasYieldAnEmptyRef(final JsonNode node)
    {
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", node);

        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), testNode);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> nonLegalDollarSchemas()
    {
        return ImmutableList.of(
            new Object[] { "" },
            new Object[] { "foo#" },
            new Object[] { "http://my.site/myschema#a" }
        ).iterator();
    }

    @Test(dataProvider = "nonLegalDollarSchemas")
    public void nonAbsoluteDollarSchemasYieldAnEmptyRef(final String s)
    {
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", FACTORY.textNode(s));

        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), testNode);
        assertEquals(tree.getDollarSchema(), JsonRef.emptyRef());
    }

    @DataProvider
    public Iterator<Object[]> legalDollarSchemas()
    {
        return ImmutableList.of(
            new Object[] { "http://json-schema.org/schema#" },
            new Object[] { "http://json-schema.org/draft-03/schema" },
            new Object[] { "http://json-schema.org/draft-04/schema#" },
            new Object[] { "http://me.org/myschema" }
        ).iterator();
    }

    @Test(dataProvider = "legalDollarSchemas")
    public void legalDollarSchemasAreReturnedCorrectly(final String s)
        throws JsonReferenceException
    {
        final JsonRef ref = JsonRef.fromString(s);
        final ObjectNode testNode = FACTORY.objectNode();
        testNode.put("$schema", FACTORY.textNode(s));

        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), testNode);
        assertEquals(tree.getDollarSchema(), ref);
    }

    @Test
    public void twoAnonymouslyLoadedSchemasAreNotEqualEvenIfJsonIsEqual()
        throws IOException
    {
        final JsonNode node = JsonLoader.fromResource("/draftv4/schema");
        final SchemaTree tree
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);
        final SchemaTree tree2
            = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);

        assertNotEquals(tree, tree2);
    }

    @Test
    public void danglingRefToSubschema() throws JsonPointerException
    {
        final ObjectNode node = FACTORY.objectNode();
        final SchemaTree tree
             = new CanonicalSchemaTree(SchemaKey.anonymousKey(), node);
        assertNotEquals(tree, null);
        final SchemaTree subtree = tree.setPointer(new JsonPointer("/missing"));
        assertNotEquals(subtree, null);
        assertNotEquals(subtree.getNode(), null);
        assertEquals(subtree.getNode().isMissingNode(), true);
    }
}
