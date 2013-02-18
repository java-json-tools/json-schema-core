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
import com.fasterxml.jackson.databind.node.MissingNode;
import com.github.fge.jsonschema.exceptions.JsonPointerException;
import com.github.fge.jsonschema.messages.JsonReferenceMessages;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * A pointer into a {@link TreeNode}
 *
 * <p>Note that all pointers are <b>absolute</b>: they start from the root of
 * the tree. This is to mirror the behaviour of JSON Pointer proper.</p>
 *
 * <p>The class does not decode a JSON Pointer representation itself; however
 * it provides all the necessary methods for implementations to achieve this.
 * </p>
 *
 * <p>This class has two traversal methods: {@link #get(TreeNode)} and {@link
 * #path(TreeNode)}. The difference between both is that {@code path()} may
 * return another node than {@code null} if the tree representation has such
 * a node. This is the case, for instance, for {@link JsonNode}, which has a
 * {@link MissingNode}.</p>
 *
 * <p>At the core, this class is essentially a(n ordered!) {@link List} of
 * {@link TokenResolver}s (which is iterable via the class itself).</p>
 *
 * <p>Note that this class' {@link #hashCode()}, {@link #equals(Object)} and
 * {@link #toString()} are final.</p>
 *
 * @param <T> the type of the tree
 */
public abstract class TreePointer<T extends TreeNode>
    implements Iterable<TokenResolver<T>>
{
    /**
     * The reference token separator
     */
    protected static final char SLASH = '/';

    /**
     * What this tree can see as a missing node (may be {@code null})
     */
    protected final T missing;

    /**
     * The list of token resolvers
     */
    protected final List<TokenResolver<T>> tokenResolvers;

    /**
     * Main protected constructor
     *
     * <p>This constructor makes an immutable copy of the list it receives as
     * an argument.</p>
     *
     * @param missing the representation of a missing node
     * @param tokenResolvers the list of reference token resolvers
     */
    protected TreePointer(final T missing,
        final List<TokenResolver<T>> tokenResolvers)
    {
        this.missing = missing;
        this.tokenResolvers = ImmutableList.copyOf(tokenResolvers);
    }

    /**
     * Alternate constructor
     *
     * <p>This is the same as calling {@link #TreePointer(TreeNode, List)} with
     * {@code null} as the missing node.</p>
     *
     * @param tokenResolvers the list of token resolvers
     */
    protected TreePointer(final List<TokenResolver<T>> tokenResolvers)
    {
        this(null, tokenResolvers);
    }

    /**
     * Decode an input into a list of reference tokens
     *
     * @param input the input
     * @return the list of reference tokens
     * @throws JsonPointerException input is not a valid JSON Pointer
     */
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

    /**
     * Traverse a node and return the result
     *
     * <p>Note that this method shortcuts: it stops at the first node it cannot
     * traverse.</p>
     *
     * @param node the node to traverse
     * @return the resulting node, {@code null} if not found
     */
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

    /**
     * Traverse a node and return the result
     *
     * <p>This is like {@link #get(TreeNode)}, but it will return the missing
     * node if traversal fails.</p>
     *
     * @param node the node to traverse
     * @return the result, or the missing node
     * @see #TreePointer(TreeNode, List)
     */
    public final T path(final T node)
    {
        final T ret = get(node);
        return ret == null ? missing : ret;
    }

    /**
     * Tell whether this pointer is empty
     *
     * @return true if the reference token list is empty
     */
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
        /*
         * This works fine: a TokenResolver's .toString() always returns the
         * cooked representation of its underlying ReferenceToken.
         */
        for (final TokenResolver<T> tokenResolver: tokenResolvers)
            sb.append('/').append(tokenResolver);

        return sb.toString();
    }
}
