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
 * will invoke a listener at various points in time when it walks the schema.
 * </p>
 *
 * <p>All methods can throw a {@link ProcessingException} if you choose to
 * abort processing due to an anomalous condition.</p>
 *
 * @param <T> the value type produced by this listener
 */
public interface SchemaListener<T>
{
    /**
     * Method called before schema walking commences
     *
     * @param tree the walked schema tree
     * @throws ProcessingException processing failure
     */
    void onInit(final SchemaTree tree)
        throws ProcessingException;

    /**
     *  Method called when the walking process changes trees
     *
     * @param oldTree the old tree
     * @param newTree the new tree
     * @throws ProcessingException processing failure
     * @see ResolvingSchemaWalker
     */
    void onNewTree(final SchemaTree oldTree, final SchemaTree newTree)
        throws ProcessingException;

    /**
     * Method called when the walker changes pointer into the current tree
     *
     * @param pointer the <b>relative</b> pointer into the tree
     * @throws ProcessingException processing failure
     */
    void onPushd(final JsonPointer pointer)
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
     * Method called when the walking process exits a subtree
     *
     * @throws ProcessingException processing failure
     */
    void onPopd()
        throws ProcessingException;

    /**
     * Method called when the walking process is done walking the tree
     *
     * @throws ProcessingException processing failure
     */
    void onExit()
        throws ProcessingException;

    /**
     * Return the value produced by this listener
     *
     * @return the value
     */
    T getValue();
}
