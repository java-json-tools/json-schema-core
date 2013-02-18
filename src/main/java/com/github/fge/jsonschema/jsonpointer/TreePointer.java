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
import com.github.fge.jsonschema.messages.JsonReferenceMessages;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

public abstract class TreePointer<T extends TreeNode>
    implements Iterable<TokenResolver<T>>
{
    protected static final char SLASH = '/';

    protected final T missing;

    protected final List<TokenResolver<T>> tokenResolvers;

    protected TreePointer(final T missing,
        final List<TokenResolver<T>> tokenResolvers)
    {
        this.missing = missing;
        this.tokenResolvers = ImmutableList.copyOf(tokenResolvers);
    }

    protected TreePointer(final List<TokenResolver<T>> tokenResolvers)
    {
        this(null, tokenResolvers);
    }

    protected static List<ReferenceToken> tokensFromInput(final String input)
        throws JsonPointerException
    {
        final List<ReferenceToken> ret = Lists.newArrayList();
        String s = input;
        String cooked;
        int index;
        char c;

        while (!s.isEmpty()) {
            c = s.charAt(0);
            if (c != SLASH)
                throw new JsonPointerException(new ProcessingMessage()
                    .message(JsonReferenceMessages.NOT_SLASH)
                    .put("expected", Character.valueOf(SLASH))
                    .put("found", Character.valueOf(c)));
            s = s.substring(1);
            index = s.indexOf(SLASH);
            cooked = index == -1 ? s : s.substring(0, index);
            ret.add(ReferenceToken.fromCooked(cooked));
            if (index == -1)
                break;
            s = s.substring(index);
        }

        return ret;
    }

    public final T get(final T node)
    {
        T ret = node;
        for (final TokenResolver<T> tokenResolver: tokenResolvers) {
            if (ret == null)
                break;
            ret = tokenResolver.get(ret);
        }

        return ret;
    }

    public final T path(final T node)
    {
        final T ret = get(node);
        return ret == null ? missing : ret;
    }

    public final boolean isEmpty()
    {
        return tokenResolvers.isEmpty();
    }

    @Override
    public final Iterator<TokenResolver<T>> iterator()
    {
        return tokenResolvers.iterator();
    }

    @Override
    public final int hashCode()
    {
        return tokenResolvers.hashCode();
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
        final TreePointer<T> other = (TreePointer<T>) obj;
        return tokenResolvers.equals(other.tokenResolvers);
    }

    @Override
    public final String toString()
    {
        final StringBuilder sb = new StringBuilder();
        for (final TokenResolver<T> tokenResolver: tokenResolvers)
            sb.append('/').append(tokenResolver);

        return sb.toString();
    }
}
