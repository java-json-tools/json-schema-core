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

package com.github.fge.jsonschema.core.misc.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.equivalence.SchemaTreeEquivalence;
import com.google.common.base.Equivalence;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
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
        final ProcessingReport report = mock(ProcessingReport.class);

        final SchemaExpander expander = new SchemaExpander(orig);
        expander.enteringPath(ptr, report);
        expander.visiting(newTree, report);
        expander.exitingPath(ptr, report);

        assertTrue(EQUIVALENCE.equivalent(expander.getValue(), expected));
    }
}
