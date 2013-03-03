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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.assertEquals;

public final class SimpleSchemaWalkerTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private static final String K1 = "k1";
    private static final String K2 = "k2";

    private PointerCollector collector1;
    private PointerCollector collector2;

    private SchemaListener<Object> listener;
    private ProcessingReport report;

    @BeforeMethod
    @SuppressWarnings("unchecked")
    public void init()
    {
        collector1 = mock(PointerCollector.class);
        collector2 = mock(PointerCollector.class);
        listener = mock(SchemaListener.class);
        report = mock(ProcessingReport.class);
    }

    @Test
    public void listenerIsCalledOnEnterWalkAndExit()
        throws ProcessingException
    {
        final Dictionary<PointerCollector> dict
            = Dictionary.<PointerCollector>newBuilder().freeze();

        final SchemaTree tree = new CanonicalSchemaTree(FACTORY.objectNode());
        final SchemaWalker walker = new SimpleSchemaWalker(tree, dict);

        walker.walk(listener, report);
        final InOrder order = inOrder(listener);
        order.verify(listener).onInit(same(tree));
        order.verify(listener).onWalk(same(tree));
        order.verify(listener).onExit();
    }

    @Test
    public void collectorsAreCalledWhenAppropriate()
        throws ProcessingException
    {
        final Dictionary<PointerCollector> dict
            = Dictionary.<PointerCollector>newBuilder().addEntry(K1, collector1)
            .addEntry(K2, collector2).freeze();

        final ObjectNode schema = FACTORY.objectNode().put(K1, K1);
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final SchemaWalker walker = new SimpleSchemaWalker(tree, dict);

        walker.walk(listener, report);
        verify(collector1).collect(anyCollectionOf(JsonPointer.class),
            same(tree));
        verify(collector2, never()).collect(anyCollectionOf(JsonPointer.class),
            any(SchemaTree.class));
    }

    @Test
    public void listenerIsCalledWhenChangingPointers()
        throws ProcessingException
    {
        final JsonPointer pointer = JsonPointer.of(K1);

        final PointerCollector collector = new PointerCollector()
        {
            @Override
            public void collect(final Collection<JsonPointer> pointers,
                final SchemaTree tree)
            {
                pointers.add(pointer);
            }
        };

        final ObjectNode subNode = FACTORY.objectNode();
        final ObjectNode schema = FACTORY.objectNode();
        schema.put(K1, subNode);

        final Dictionary<PointerCollector> dict
            = Dictionary.<PointerCollector>newBuilder()
            .addEntry(K1, collector).freeze();
        final SchemaTree tree = new CanonicalSchemaTree(schema);
        final SchemaWalker walker = new SimpleSchemaWalker(tree, dict);

        final ArgumentCaptor<SchemaTree> captor
            = ArgumentCaptor.forClass(SchemaTree.class);

        walker.walk(listener, report);

        final InOrder order = inOrder(listener);
        order.verify(listener).onPushd(same(pointer));
        order.verify(listener).onWalk(captor.capture());
        order.verify(listener).onPopd();

        final SchemaTree subTree = captor.getValue();
        assertEquals(subTree.getNode(), subNode);
        assertEquals(subTree.getPointer(), pointer);
    }
}

