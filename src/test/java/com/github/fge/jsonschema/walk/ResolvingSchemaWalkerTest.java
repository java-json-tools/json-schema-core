/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.walk;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.SchemaWalkingException;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.messages.JsonSchemaSyntaxMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.DevNullProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ResolvingSchemaWalkerTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void listenerIsCalledAppropriatelyOnTreeChange()
        throws ProcessingException
    {
        final String uri1 = "x://y/s1#";
        final String uri2 = "x://y/s2#";
        final ObjectNode schema1 = JacksonUtils.nodeFactory().objectNode()
            .put("$ref", uri2);
        final ObjectNode schema2 = JacksonUtils.nodeFactory().objectNode();

        final SchemaTree tree = new CanonicalSchemaTree(schema1);

        final LoadingConfiguration loadingCfg
            = LoadingConfiguration.newBuilder()
                .preloadSchema(uri1, schema1).preloadSchema(uri2, schema2)
                .freeze();
        final SchemaWalkingConfiguration cfg
            = SchemaWalkingConfiguration.newBuilder()
                .setLoadingConfiguration(loadingCfg).freeze();

        final SchemaWalker walker = new ResolvingSchemaWalker(tree, cfg);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        final InOrder order = inOrder(listener);
        final ArgumentCaptor<SchemaTree> captor
            = ArgumentCaptor.forClass(SchemaTree.class);

        walker.walk(listener, report);

        order.verify(listener).enteringPath(JsonPointer.empty(), report);
        order.verify(listener).visiting(captor.capture(), same(report));
        order.verify(listener).exitingPath(JsonPointer.empty(), report);

        final SchemaTree subTree = captor.getValue();
        assertEquals(subTree.getNode(), schema2);
    }

    @Test
    public void walkerRefusesToExpandToChildTree()
        throws ProcessingException
    {
        final ObjectNode subSchema = JacksonUtils.nodeFactory().objectNode();
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode()
            .put("$ref", "#/a");
        schema.put("a", subSchema);
        final JsonRef ref = JsonRef.fromString("x://y/z#");
        final SchemaTree tree = new CanonicalSchemaTree(ref, schema);

        final SchemaWalker walker = new ResolvingSchemaWalker(tree);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (SchemaWalkingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.getMessage("schemaWalking.subtreeExpand"))
                .hasField("schemaURI", ref)
                .hasField("source", JsonPointer.empty())
                .hasField("target", JsonPointer.of("a"));
        }
    }

    @Test
    public void walkerRefusesToExpandToParentTree()
        throws ProcessingException
    {
        final ObjectNode subSchema = JacksonUtils.nodeFactory().objectNode()
            .put("$ref", "#");
        final ObjectNode schema = JacksonUtils.nodeFactory().objectNode();
        schema.put("not", subSchema);
        final JsonRef ref = JsonRef.fromString("x://y/z#");
        final SchemaTree tree = new CanonicalSchemaTree(ref, schema);

        final SchemaWalker walker = new ResolvingSchemaWalker(tree);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (SchemaWalkingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.getMessage("schemaWalking.parentExpand"))
                .hasField("schemaURI", ref)
                .hasField("source", JsonPointer.of("not"))
                .hasField("target", JsonPointer.empty());
        }
    }

    @Test
    public void newTreesAreCheckedForSyntax()
        throws ProcessingException
    {
        final String uri = "x://y/z#";
        final ObjectNode schema1 = JacksonUtils.nodeFactory().objectNode()
            .put("$ref", uri);
        final ObjectNode schema2 = JacksonUtils.nodeFactory().objectNode();
        schema2.put("not", "inMyLife");
        final SchemaTree tree = new CanonicalSchemaTree(schema1);

        final LoadingConfiguration loadingCfg
            = LoadingConfiguration.newBuilder().preloadSchema(uri, schema2)
                .freeze();
        final SchemaWalkingConfiguration cfg
            = SchemaWalkingConfiguration.newBuilder()
                .setLoadingConfiguration(loadingCfg).freeze();

        final SchemaWalker walker = new ResolvingSchemaWalker(tree, cfg);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (InvalidSchemaException e) {
            final MessageBundle bundle
                = MessageBundles.getBundle(JsonSchemaSyntaxMessageBundle.class);
            assertMessage(e.getProcessingMessage())
                .hasMessage(bundle.getMessage("core.invalidSchema"));
        }
    }
}
