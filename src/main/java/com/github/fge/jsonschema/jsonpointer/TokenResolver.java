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

package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.core.TreeNode;
import com.fasterxml.jackson.databind.JsonNode;
import net.jcip.annotations.ThreadSafe;

/**
 * Reference token traversal class
 *
 * <p>This class is meant to be extended and implemented for all types of trees
 * inheriting {@link TreeNode}.</p>
 *
 * <p>This package contains one implementation of this class for {@link
 * JsonNode}.</p>
 *
 * <p>Note that its {@link #equals(Object)}, {@link #hashCode()} and {@link
 * #toString()} are final.</p>
 *
 * @param <T> the type of tree to traverse
 *
 * @see JsonNodeResolver
 */
@ThreadSafe
public abstract class TokenResolver<T extends TreeNode>
{
    /**
     * The associated reference token
     */
    protected final ReferenceToken token;

    /**
     * The only constructor
     *
     * @param token the reference token
     */
    protected TokenResolver(final ReferenceToken token)
    {
        this.token = token;
    }

    /**
     * Advance one level into the tree
     *
     * <p>Note: it is <b>required</b> that this method return null on
     * traversal failure.</p>
     *
     * @param node the node to traverse
     * @return the other node, or {@code null} if no such node exists for that
     * token
     */
    public abstract T get(final T node);

    @Override
    public final int hashCode()
    {
        return token.hashCode();
    }

    @Override
    public final boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        @SuppressWarnings("unchecked")
        final TokenResolver<T> other = (TokenResolver<T>) obj;
        return token.equals(other.token);
    }

    @Override
    public final String toString()
    {
        return token.toString();
    }
}
