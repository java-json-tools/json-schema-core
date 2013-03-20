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

/**
 * JSON Pointer related classes
 *
 * <p>This package, while primarily centered on {@link
 * com.github.fge.jackson.jsonpointer.JsonPointer}, is a generalization of JSON
 * Pointer to all implementations of Jackson's {@link
 * com.fasterxml.jackson.core.TreeNode}.</p>
 *
 * <p>The fundamentals of JSON Pointer remain the same, however: a JSON pointer
 * is a set of reference tokens separated by the {@code /} character. One
 * reference token is materialized by the {@link
 * com.github.fge.jackson.jsonpointer.ReferenceToken} class, and advancing
 * one level into a tree is materialized by {@link
 * com.github.fge.jackson.jsonpointer.TokenResolver}. A {@link
 * com.github.fge.jackson.jsonpointer.TreePointer} is a collection of token
 * resolvers.</p>
 */
package com.github.fge.jackson.jsonpointer;
