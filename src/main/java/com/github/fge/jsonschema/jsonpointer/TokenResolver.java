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

public abstract class TokenResolver<T extends TreeNode>
{
    protected final ReferenceToken token;

    protected TokenResolver(final ReferenceToken token)
    {
        this.token = token;
    }

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
