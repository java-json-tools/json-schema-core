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

package com.github.fge.jsonschema.core.misc.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.walk.SchemaListener;
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
    public void visiting(final SchemaTree schemaTree,
        final ProcessingReport report)
        throws ProcessingException
    {
        /*
         * Check whether our current node is equal to the current node of the
         * provided tree. If it is, nothing to do.
         */
        final JsonNode node = schemaTree.getNode();
        if (EQUIVALENCE.equivalent(node, currentNode))
            return;

        /*
         * If not, make a copy of it.
         */
        final JsonNode newNode = node.deepCopy();

        /*
         * If this is the root, just replace unconditionally.
         */
        if (path.isEmpty()) {
            baseNode = currentNode = newNode;
            return;
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
