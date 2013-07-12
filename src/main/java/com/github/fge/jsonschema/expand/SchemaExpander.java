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

package com.github.fge.jsonschema.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.SchemaListener;
import com.google.common.annotations.Beta;
import com.google.common.base.Equivalence;
import com.google.common.collect.Iterables;
import com.google.common.collect.Queues;

import java.util.Deque;

@Beta
public final class SchemaExpander
    implements SchemaListener<SchemaTree>
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    private final JsonRef baseRef;
    private JsonNode baseNode = MissingNode.getInstance();
    private JsonNode currentNode;

    private JsonPointer path = JsonPointer.empty();
    private final Deque<JsonPointer> paths = Queues.newArrayDeque();

    public SchemaExpander(final SchemaTree tree)
    {
        baseRef = tree.getLoadingRef();
        baseNode = tree.getBaseNode().deepCopy();
    }

    @Override
    public void enteringPath(final JsonPointer path,
        final ProcessingReport report)
        throws ProcessingException
    {
        paths.push(path);
        this.path = path;
        currentNode = path.get(baseNode);
    }

    @Override
    public boolean visiting(final SchemaTree schemaTree,
        final ProcessingReport report)
        throws ProcessingException
    {
        /*
         * Check whether our current node is equal to the current node of the
         * provided tree. If it is, nothing to do.
         */
        final JsonNode node = schemaTree.getNode();
        if (EQUIVALENCE.equivalent(node, currentNode))
            return true;

        /*
         * If not, make a copy of it.
         */
        final JsonNode newNode = node.deepCopy();

        /*
         * If this is the root, just replace unconditionally.
         */
        if (path.isEmpty()) {
            baseNode = currentNode = newNode;
            return true;
        }

        /*
         * Otherwise, get the parent of the current node, replace it.
         */
        final JsonPointer parent = path.parent();
        final String token = getLastToken(path);
        final JsonNode parentNode = parent.get(baseNode);
        final NodeType type = NodeType.getNodeType(parentNode);

        switch (type) {
            case OBJECT:
                ((ObjectNode) parentNode).put(token, newNode);
                break;
            case ARRAY:
                ((ArrayNode) parentNode).set(Integer.parseInt(token), newNode);
                break;
            default:
                throw new IllegalStateException("was expecting an object or" +
                    " an array");
        }

        /*
         * Finally, update our current node.
         */
        currentNode = path.get(baseNode);
        return true;
    }

    @Override
    public void exitingPath(final JsonPointer path,
        final ProcessingReport report)
        throws ProcessingException
    {
        this.path = paths.pop();
        currentNode = this.path.get(baseNode);
    }

    @Override
    public SchemaTree getValue()
    {
        return new CanonicalSchemaTree(baseRef, baseNode);
    }

    private static String getLastToken(final JsonPointer ptr)
    {
        return Iterables.getLast(ptr).getToken().getRaw();
    }
}
