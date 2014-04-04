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
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
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
