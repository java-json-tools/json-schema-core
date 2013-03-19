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

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.tree.SchemaTree;

/**
 * A schema walker listener
 *
 * <p>This is the main working part of a walking process. A {@link SchemaWalker}
 * will invoke a listener at various points in time when it walks a schema tree.
 * </p>
 *
 * <p>All methods can throw a {@link ProcessingException} if you choose to
 * abort processing due to an anomalous condition.</p>
 *
 * <p>For one subtree, the order in which events are called are:</p>
 *
 * <ul>
 *     <li>{@link #onEnter(JsonPointer)} (visiting a subtree);</li>
 *     <li>{@link #onTreeChange(SchemaTree, SchemaTree)} (only if {@link
 *     ResolvingSchemaWalker} is used);</li>
 *     <li>{@link #onWalk(SchemaTree)};</li>
 *     <li>{@link #onExit(JsonPointer)} (exiting a subtree).</li>
 * </ul>
 *
 * <p>For instance, if we consider this schema:</p>
 *
 * <pre>
 *     {
 *         "type": "array",
 *         "items": { "type": "string" }
 *     }
 * </pre>
 *
 * <p>for a listener {@code listener}, the order of events will be:</p>
 *
 * <pre>
 *     // Note: JSON Pointers used for both pointers and trees
 *     listener.onEnter("");
 *     listener.onWalk("");
 *     listener.onEnter("/items");
 *     listener.onWalk("/items");
 *     listener.onExit();
 *     listener.onExit();
 * </pre>
 *
 * @param <T> the value type produced by this listener
 */
public interface SchemaListener<T>
{
    /**
     *  Method called when the walking process changes trees
     *
     * @param oldTree the old tree
     * @param newTree the new tree
     * @throws ProcessingException processing failure
     * @see ResolvingSchemaWalker
     */
    void onTreeChange(final SchemaTree oldTree, final SchemaTree newTree)
        throws ProcessingException;

    /**
     * Method called when the current tree node is walked
     *
     * @param tree the current tree
     * @throws ProcessingException processing failure
     */
    void onWalk(final SchemaTree tree)
        throws ProcessingException;

    /**
     * Method called when the walker changes pointer into the currently walked
     * tree
     *
     * @param pointer the <b>relative</b> pointer into the tree
     * @throws ProcessingException processing failure
     */
    void onEnter(final JsonPointer pointer)
        throws ProcessingException;

    /**
     * Method called when the walking process exits a subtree
     *
     * @param pointer the <b>relative</b> pointer into the tree
     * @throws ProcessingException processing failure
     */
    void onExit(final JsonPointer pointer)
        throws ProcessingException;

    /**
     * Return the value produced by this listener
     *
     * @return the value
     */
    T getValue();
}
