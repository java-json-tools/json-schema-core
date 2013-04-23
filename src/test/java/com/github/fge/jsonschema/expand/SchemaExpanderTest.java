package com.github.fge.jsonschema.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.equivalence.SchemaTreeEquivalence;
import com.google.common.base.Equivalence;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class SchemaExpanderTest
{
    private static final JsonRef REF;
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private static final Equivalence<SchemaTree> EQUIVALENCE
        = SchemaTreeEquivalence.getInstance();

    static {
        try {
            REF = JsonRef.fromString("foo://bar");
        } catch (JsonReferenceException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    @Test
    public void ifNoTreeChangeOutputTreeIsTheSame()
    {
        final ObjectNode node = FACTORY.objectNode();
        node.put("foo", "bar");

        final SchemaTree tree = new CanonicalSchemaTree(REF, node);
        final SchemaExpander expander = new SchemaExpander(tree);
        assertTrue(EQUIVALENCE.equivalent(tree, expander.getValue()));
    }

    @Test
    public void ifTreeChangeOutputIsModified()
        throws ProcessingException
    {
        final String foo = "foo";
        final JsonNode baz = FACTORY.textNode("baz");
        ObjectNode node;

        /*
         * Build the original schema
         */
        node = FACTORY.objectNode();
        node.put(foo, "bar");
        final SchemaTree orig = new CanonicalSchemaTree(REF, node);

        /*
         * Build the expected schema
         */
        node = FACTORY.objectNode();
        node.put(foo, baz);
        final SchemaTree expected = new CanonicalSchemaTree(REF, node);

        // OK, this is cheating but it works
        final JsonPointer ptr = JsonPointer.of(foo);
        final SchemaTree newTree = new CanonicalSchemaTree(baz);

        final SchemaExpander expander = new SchemaExpander(orig);
        expander.onEnter(ptr);
        expander.onTreeChange(null, newTree);
        expander.onExit(ptr);

        assertTrue(EQUIVALENCE.equivalent(expander.getValue(), expected));
    }
}
