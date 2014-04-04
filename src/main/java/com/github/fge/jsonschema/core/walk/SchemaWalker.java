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

package com.github.fge.jsonschema.core.walk;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.google.common.annotations.Beta;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;

/**
 * Main schema walker class
 *
 * <p>This class walks a JSON Schema (in the shape of a {@link SchemaTree}
 * recursively. In order to visit subschemas, it relies on a series of {@link
 * PointerCollector} instances (provided by a dictionary) to get the knowledge
 * of what schemas to visit next.</p>
 *
 * <p>Only subschemas are visited: unknown keywords, or keywords not having any
 * subschemas, are ignored.</p>
 *
 * <p><b>Important</b>: the initial schema <b>must</b> be syntactically valid.
 * </p>
 */
@Beta
public abstract class SchemaWalker
{
    /**
     * The list of pointer collectors
     */
    private final Map<String, PointerCollector> collectors;

    protected SchemaWalker(final SchemaDescriptor descriptor)
    {
        collectors = descriptor.getPointerCollectors();
    }

    /**
     * Walk a tree with a listener
     *
     * @param tree the schema tree to walk
     * @param listener the listener
     * @param report the processing report to use
     * @param <T> the value type produced by the listener
     * @throws ProcessingException processing failure
     */
    public final <T> void walk(final SchemaTree tree,
        final SchemaListener<T> listener,
        final ProcessingReport report)
        throws ProcessingException
    {
        walkTree(JsonPointer.empty(), tree, listener, report);
    }

    protected abstract SchemaTree resolveTree(final SchemaTree tree,
        final ProcessingReport report)
        throws ProcessingException;

    private <T> void walkTree(final JsonPointer pwd, final SchemaTree tree,
        final SchemaListener<T> listener, final ProcessingReport report)
        throws ProcessingException
    {
        listener.enteringPath(pwd, report);
        final SchemaTree resolvedTree = resolveTree(tree, report);
        listener.visiting(resolvedTree, report);

        /*
         * Grab pointer collectors
         */
        final Map<String, PointerCollector> map = Maps.newTreeMap();

        map.putAll(collectors);
        map.keySet().retainAll(Sets.newHashSet(
            resolvedTree.getNode().fieldNames()));

        /*
         * Collect pointers to visit next
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final PointerCollector collector: map.values())
            collector.collect(pointers, resolvedTree);

        /*
         * Now, visit the collected set of pointers
         */
        for (final JsonPointer ptr: pointers) {
            walkTree(pwd.append(ptr), resolvedTree.append(ptr), listener,
                report);
        }
        listener.exitingPath(pwd, report);
    }

    @Override
    public abstract String toString();
}
