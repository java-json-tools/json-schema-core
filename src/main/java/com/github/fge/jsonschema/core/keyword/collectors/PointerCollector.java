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

package com.github.fge.jsonschema.core.keyword.collectors;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.walk.SchemaWalker;

import java.util.Collection;

/**
 * Interface for a pointer collector
 *
 * <p>This interface is the core piece of a {@link SchemaWalker}. One such
 * collector exists for each schema keyword which has subschemas, and
 * implementations add pointers to the collection passed as an argument if any.
 * </p>
 */
public interface PointerCollector
{
    /**
     * Collect pointers for the current schema node
     *
     * @param pointers the pointer collection to add to
     * @param tree the current schema node
     */
    void collect(final Collection<JsonPointer> pointers, final SchemaTree tree);
}
