package com.github.fge.jsonschema.walk;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.testng.annotations.Test;

import java.util.Collection;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaVisitorTest
{
    @Test
    public void schemaVisitorIsCalledOnTraversedPaths()
        throws ProcessingException
    {
        final JsonNodeFactory factory = JacksonUtils.nodeFactory();

        final ObjectNode subSchema = factory.objectNode();
        subSchema.put("d", "e");

        // { "a": "b", "c": { "d": "e" } }
        final ObjectNode schema = factory.objectNode();
        schema.put("a", "b");
        schema.put("c", subSchema);

        final SchemaTree tree = new CanonicalSchemaTree(schema);

        final PointerCollector collector = new TestCollector();

        final Dictionary<PointerCollector> dictionary
            = Dictionary.<PointerCollector>newBuilder()
            .addEntry("c", collector).freeze();

        final SchemaWalker2 walker = new TestWalker(dictionary);

        @SuppressWarnings("unchecked")
        final SchemaVisitor<Object> visitor = mock(SchemaVisitor.class);

        walker.accept(tree, visitor);

        final InOrder inOrder = inOrder(visitor);
        final ArgumentCaptor<SchemaTree> captor
            = ArgumentCaptor.forClass(SchemaTree.class);

        inOrder.verify(visitor).enteringPath(JsonPointer.empty());
        inOrder.verify(visitor).visitingPath(same(tree));
        inOrder.verify(visitor).enteringPath(JsonPointer.of("c"));
        inOrder.verify(visitor).visitingPath(captor.capture());
        inOrder.verify(visitor).exitingPath(JsonPointer.of("c"));
        inOrder.verify(visitor).exitingPath(JsonPointer.empty());
        inOrder.verifyNoMoreInteractions();

        final SchemaTree subTree = captor.getValue();
        assertTrue(JsonNumEquals.getInstance().equivalent(subTree.getNode(),
            subSchema));
    }

    private static final class TestCollector
        implements PointerCollector
    {
        @Override
        public void collect(final Collection<JsonPointer> pointers,
            final SchemaTree tree)
        {
            pointers.add(JsonPointer.of("c"));
        }
    }

    private static final class TestWalker
        extends SchemaWalker2
    {
        private TestWalker(final Dictionary<PointerCollector> dict)
        {
            super(dict);
        }

        @Override
        protected SchemaTree resolveTree(final SchemaTree tree)
            throws ProcessingException
        {
            return tree;
        }
    }
}
