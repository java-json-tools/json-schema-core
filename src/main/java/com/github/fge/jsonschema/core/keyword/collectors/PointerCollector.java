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
