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

package com.github.fge.jsonschema.core.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.processing.RawProcessor;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
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
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

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
                    .setMessage(BUNDLE.getMessage("refProcessing.refLoop"))
                    .put("schema", tree).putArgument("ref", ref)
                    .put("path", refs));
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
                    .setMessage(BUNDLE.getMessage("refProcessing.danglingRef"))
                    .put("schema", tree).putArgument("ref", ref));
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
