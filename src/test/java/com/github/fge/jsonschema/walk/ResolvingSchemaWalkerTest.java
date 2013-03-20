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
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.SchemaWalkingException;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.SyntaxMessages;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.DevNullProcessingReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.SchemaWalkerMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class ResolvingSchemaWalkerTest
{
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

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(uri1, schema1).preloadSchema(uri2, schema2).freeze();

        final SchemaWalker walker
            = new ResolvingSchemaWalker(tree, SchemaVersion.DRAFTV4, cfg);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        final InOrder order = inOrder(listener);
        final ArgumentCaptor<SchemaTree> captor
            = ArgumentCaptor.forClass(SchemaTree.class);
        final ArgumentCaptor<SchemaTree> captor2
            = ArgumentCaptor.forClass(SchemaTree.class);

        walker.walk(listener, report);

        order.verify(listener).onEnter(JsonPointer.empty());
        order.verify(listener).onTreeChange(same(tree), captor.capture());
        order.verify(listener).onWalk(captor2.capture());
        order.verify(listener).onExit(JsonPointer.empty());

        final SchemaTree subTree = captor.getValue();
        final SchemaTree subTree2 = captor2.getValue();
        assertEquals(subTree.getNode(), schema2);
        assertSame(subTree, subTree2);
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

        final SchemaWalker walker
            = new ResolvingSchemaWalker(tree, SchemaVersion.DRAFTV4);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (SchemaWalkingException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(SUBTREE_EXPAND)
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

        final SchemaWalker walker
            = new ResolvingSchemaWalker(tree, SchemaVersion.DRAFTV4);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (SchemaWalkingException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(PARENT_EXPAND)
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

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(uri, schema2).freeze();

        final SchemaWalker walker
            = new ResolvingSchemaWalker(tree, SchemaVersion.DRAFTV4, cfg);

        @SuppressWarnings("unchecked")
        final SchemaListener<Object> listener = mock(SchemaListener.class);
        final ProcessingReport report = new DevNullProcessingReport();

        try {
            walker.walk(listener, report);
            fail("No exception thrown!!");
        } catch (InvalidSchemaException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(SyntaxMessages.INVALID_SCHEMA);
        }
    }
}
