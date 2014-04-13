/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.tree;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.ref.JsonRef;

/**
 * Tree representation of a JSON Schema
 *
 * <p>In addition to navigation capabilities and node retrieval, this tree
 * returns URI context information and JSON Reference resolution.</p>
 */
public interface SchemaTree
    extends SimpleTree
{
    /**
     * Relocate the tree relatively to the current tree's pointer
     *
     * @param pointer the pointer to append
     * @return a new tree
     * @see JsonPointer#append(JsonPointer)
     */
    SchemaTree append(final JsonPointer pointer);

    /**
     * Relocate the tree with an absolute pointer
     *
     * @param pointer the pointer
     * @return a new tree
     */
    SchemaTree setPointer(final JsonPointer pointer);

    /**
     * Resolve a JSON Reference against the current resolution context
     *
     * @param other the JSON Reference to resolve
     * @return the resolved reference
     * @see JsonRef#resolve(JsonRef)
     */
    JsonRef resolve(final JsonRef other);

    /**
     * Tell whether a JSON Reference is contained within this schema tree
     *
     * <p>This method will return {@code true} if the caller can <i>attempt</i>
     * to retrieve the JSON value addressed by this reference from the schema
     * tree directly.</p>
     *
     * <p>Note that the reference <b>must</b> be fully resolved for this method
     * to work.</p>
     *
     * @param ref the target reference
     * @return see description
     * @see #resolve(JsonRef)
     */
    boolean containsRef(final JsonRef ref);

    /**
     * Return a matching pointer in this tree for a fully resolved reference
     *
     * <p>This must be called <b>only</b> when {@link #containsRef(JsonRef)}
     * returns {@code true}. Otherwise, its result is undefined.</p>
     *
     * @param ref the reference
     * @return the matching pointer, or {@code null} if not found
     */
    JsonPointer matchingPointer(final JsonRef ref);

    long getId();

    /**
     * Return the metaschema URI for that schema (ie, {@code $schema})
     *
     * <p>Note: it is <b>required</b> that if present, {@code $schema} be an
     * absolute JSON Reference. If this keyword is not present and/or is
     * malformed, an empty reference is returned.</p>
     *
     * @return the contents of {@code $schema} as a {@link JsonRef}
     */
    JsonRef getDollarSchema();

    /**
     * Get the loading URI for that schema
     *
     * @return the loading URI as a {@link JsonRef}
     */
    JsonRef getLoadingRef();

    /**
     * Get the current resolution context
     *
     * @return the context as a {@link JsonRef}
     */
    JsonRef getContext();
}
