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

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.collectors.DraftV3PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
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

    /**
     * Protected constructor for a given version
     *
     * @param tree the schema tree
     * @param version the schema version
     */
    protected SchemaWalker(final SchemaTree tree, final SchemaVersion version)
    {
        collectors = version == SchemaVersion.DRAFTV4
            ? DraftV4PointerCollectorDictionary.get().entries()
            : DraftV3PointerCollectorDictionary.get().entries();
        this.tree = tree;
    }

    /**
     * Protected constructor with a custom pointer collector dictionary
     *
     * @param tree the schema tree
     * @param dict the dictionary of pointer collectors
     */
    protected SchemaWalker(final SchemaTree tree,
        final Dictionary<PointerCollector> dict)
    {
        collectors = dict.entries();
        this.tree = tree;
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
        listener.onInit(tree);
        doWalk(listener, report);
        listener.onExit();
    }

    /**
     * Change the current tree to another tree, if any
     *
     * @param listener the listener
     * @param report the report
     * @param <T> type of value produced by the listener
     * @throws ProcessingException processing failure
     * @see ResolvingSchemaWalker
     */
    public abstract <T> void resolveTree(final SchemaListener<T> listener,
        final ProcessingReport report)
        throws ProcessingException;

    private <T> void doWalk(final SchemaListener<T> listener,
        final ProcessingReport report)
        throws ProcessingException
    {
        listener.onWalk(tree);
        resolveTree(listener, report);

        final Map<String, PointerCollector> map = Maps.newTreeMap();
        map.putAll(collectors);

        map.keySet().retainAll(Sets.newHashSet(tree.getNode().fieldNames()));

        /*
         * Collect pointers for further processing.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final PointerCollector collector: map.values())
            collector.collect(pointers, tree);

        /*
         * Operate on these pointers.
         */
        SchemaTree current;
        for (final JsonPointer pointer: pointers) {
            current = tree;
            tree = tree.append(pointer);
            listener.onPushd(pointer);
            doWalk(listener, report);
            listener.onPopd();
            tree = current;
        }
    }

    @Override
    public abstract String toString();
}
