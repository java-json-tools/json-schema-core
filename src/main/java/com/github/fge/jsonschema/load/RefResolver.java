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

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.CoreMessageBundle;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.RawProcessor;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * JSON Reference processor
 *
 * <p>This is the first, and probably the most important, processor to run in
 * the validation chain.</p>
 *
 * <p>Its role is to resolve all JSON References until a final document is
 * reached. It will throw an exception if a JSON Reference loop is detected, or
 * if a JSON Reference does not resolve.</p>
 *
 * <p>It relies on a {@link SchemaLoader} to load JSON References which are not
 * resolvable within the current schema itself.</p>
 */
public final class RefResolver
    extends RawProcessor<SchemaTree, SchemaTree>
{
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

    private final SchemaLoader loader;

    public RefResolver(final SchemaLoader loader)
    {
        super("schema", "schema");
        this.loader = loader;
    }

    @Override
    public SchemaTree rawProcess(final ProcessingReport report,
        final SchemaTree input)
        throws ProcessingException
    {
        /*
         * The set of refs we see during ref resolution, necessary to detect ref
         * loops. We make it linked since we want the ref path reported in the
         * order where refs have been encountered.
         */
        final Set<JsonRef> refs = Sets.newLinkedHashSet();

        SchemaTree tree = input;

        JsonPointer ptr;
        JsonRef ref;
        JsonNode node;

        while (true) {
            /*
             * See if the current node is a JSON Reference.
             */
            node = tree.getNode();
            /*
             * If it isn't, we are done
             */
            ref = nodeAsRef(node);
            if (ref == null)
                break;
            /*
             * Resolve the reference against the current tree.
             */
            ref = tree.resolve(ref);
            /*
             * If we have seen this ref already, this is a ref loop.
             */
            if (!refs.add(ref))
                throw new ProcessingException(new ProcessingMessage()
                    .message(BUNDLE.getKey("refProcessing.refLoop"))
                    .put("schema", tree).put("ref", ref).put("path", refs));
            /*
             * Check whether ref is resolvable within the current tree. If not,
             * fetch the new tree.
             *
             * This may fail, in which case we exit here since SchemaLoader's
             * .get() throws a ProcessingException if it fails.
             */
            if (!tree.containsRef(ref))
                tree = loader.get(ref.getLocator());
            /*
             * Get the appropriate pointer into the tree. If none, this means
             * a dangling reference.
             */
            ptr = tree.matchingPointer(ref);
            if (ptr == null)
                throw new ProcessingException(new ProcessingMessage()
                    .message(BUNDLE.getKey("refProcessing.danglingRef"))
                    .put("schema", tree).put("ref", ref));
            tree = tree.setPointer(ptr);
        }

        return tree;
    }

    private static JsonRef nodeAsRef(final JsonNode node)
    {
        final JsonNode refNode = node.path("$ref");
        if (!refNode.isTextual())
            return null;
        try {
            return JsonRef.fromString(refNode.textValue());
        } catch (JsonReferenceException ignored) {
            return null;
        }
    }

    @Override
    public String toString()
    {
        return "ref resolver";
    }
}
