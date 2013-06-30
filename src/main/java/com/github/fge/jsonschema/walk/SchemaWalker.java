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

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
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
 *
 * @see SimpleSchemaWalker
 * @see ResolvingSchemaWalker
 */
@Beta
public abstract class SchemaWalker
{
    /**
     * The current schema tree being walked
     */
    protected SchemaTree tree;

    /**
     * The list of pointer collectors
     */
    private final Map<String, PointerCollector> collectors;

    protected SchemaWalker(final SchemaTree tree,
        final SchemaWalkingConfiguration cfg)
    {
        this.tree = tree;
        collectors = cfg.collectors.entries();
    }

    /**
     * Walk a tree with a listener
     *
     * @param listener the listener
     * @param report the processing report to use
     * @param <T> the value type produced by the listener
     * @throws ProcessingException processing failure
     */
    public final <T> void walk(final SchemaListener<T> listener,
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
