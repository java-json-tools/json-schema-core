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
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.google.common.annotations.Beta;

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
 *     <li>{@link #enteringPath(JsonPointer, ProcessingReport)} (JsonPointer)}
 *     (visiting a subtree);</li>
 *     <li>{@link #visiting(SchemaTree, ProcessingReport)} (SchemaTree)};</li>
 *     <li>{@link #exitingPath(JsonPointer, ProcessingReport)} (JsonPointer)}
 *     (exiting a subtree).</li>
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
 *     listener.enteringPath("", report);
 *     listener.visiting("", report);
 *     listener.enteringPath("/items", report);
 *     listener.visiting("/items", report);
 *     listener.exitingPath("/items");
 *     listener.exitingPath("");
 * </pre>
 *
 * @param <T> the value type produced by this listener
 */
@Beta
public interface SchemaListener<T>
{
    /**
     * Method called when entering a path in the schema
     *
     * @param path the entered path
     * @param report the report to use
     * @throws ProcessingException processing failure
     */
    void enteringPath(final JsonPointer path, final ProcessingReport report)
        throws ProcessingException;

    /**
     * Method called when visiting the tree at the current path
     *
     * @param schemaTree the visited tree
     * @param report the report to use
     * @throws ProcessingException processing failure
     */
    void visiting(final SchemaTree schemaTree, final ProcessingReport report)
        throws ProcessingException;

    /**
     * Method called when exiting a path in the schema
     *
     * @param path the exited path
     * @param report the report to use
     * @throws ProcessingException processing failure
     */
    void exitingPath(final JsonPointer path, final ProcessingReport report)
        throws ProcessingException;

    /**
     * Return the value produced by this listener
     *
     * @return the value
     */
    T getValue();
}
