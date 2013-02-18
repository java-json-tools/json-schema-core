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
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.messages.JsonReferenceMessages;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.jcip.annotations.Immutable;

import java.util.List;

/**
 * A {@link TreePointer} for {@link JsonNode}
 *
 * <p>This is the "original" JSON Pointer in that it addresses JSON documents.
 * </p>
 *
 * <p>It also has a lot of utility methods covering several usage scenarios.</p>
 */
@Immutable
public final class JsonPointer
    extends TreePointer<JsonNode>
{
    /**
     * The empty JSON Pointer
     */
    private static final JsonPointer EMPTY
        = new JsonPointer(ImmutableList.<TokenResolver<JsonNode>>of());

    /**
     * Return an empty JSON Pointer
     *
     * @return an empty, statically allocated JSON Pointer
     */
    public static JsonPointer empty()
    {
        return EMPTY;
    }

    /**
     * Build a JSON Pointer out of a series of reference tokens
     *
     * <p>These tokens can be everything; be sure however that they implement
     * {@link Object#toString()} correctly!</p>
     *
     * <p>Each of these tokens are treated as <b>raw</b> tokens (ie, not
     * encoded).</p>
     *
     * @param first the first token
     * @param other other tokens
     * @return a JSON Pointer
     * @throws JsonReferenceError one input token is null
     */
    public static JsonPointer of(final Object first, final Object... other)
    {
        final List<ReferenceToken> tokens = Lists.newArrayList();

        tokens.add(ReferenceToken.fromRaw(first.toString()));

        for (final Object o: other)
            tokens.add(ReferenceToken.fromRaw(o.toString()));

        return new JsonPointer(fromTokens(tokens));
    }

    /**
     * The main constructor
     *
     * @param input the input string
     * @throws JsonReferenceException malformed JSON Pointer
     * @throws JsonReferenceError null input
     */
    public JsonPointer(final String input)
        throws JsonReferenceException
    {
        this(fromTokens(tokensFromInput(input)));
    }

    /**
     * Private constructor
     *
     * <p>This calls {@link TreePointer#TreePointer(TreeNode, List)} with a
     * {@link MissingNode} as the missing tree node.</p>
     *
     * @param tokenResolvers the list of token resolvers
     */
    private JsonPointer(final List<TokenResolver<JsonNode>> tokenResolvers)
    {
        super(MissingNode.getInstance(), tokenResolvers);
    }

    /**
     * Return a new pointer with a new token appended
     *
     * @param raw the raw token to append
     * @return a new pointer
     * @throws JsonReferenceError input is null
     */
    public JsonPointer append(final String raw)
    {
        final ReferenceToken refToken = ReferenceToken.fromRaw(raw);
        final JsonNodeResolver resolver = new JsonNodeResolver(refToken);
        final List<TokenResolver<JsonNode>> list
            = Lists.newArrayList(tokenResolvers);
        list.add(resolver);
        return new JsonPointer(list);
    }

    /**
     * Return a new pointer with a new integer token appended
     *
     * @param index the integer token to append
     * @return a new pointer
     */
    public JsonPointer append(final int index)
    {
        return append(Integer.toString(index));
    }

    /**
     * Return a new pointer with another pointer appended
     *
     * @param other the other pointer
     * @return a new pointer
     * @throws JsonReferenceError other pointer is null
     */
    public JsonPointer append(final JsonPointer other)
    {
        if (other == null)
            throw new JsonReferenceError(new ProcessingMessage()
                .message(JsonReferenceMessages.NULL_POINTER));
        final List<TokenResolver<JsonNode>> list
            = Lists.newArrayList(tokenResolvers);
        list.addAll(other.tokenResolvers);
        return new JsonPointer(list);
    }

    /**
     * Build a list of token resolvers from a list of reference tokens
     *
     * <p>Here, the token resolvers are {@link JsonNodeResolver}s.</p>
     *
     * @param tokens the token list
     * @return a (mutable) list of token resolvers
     */
    private static List<TokenResolver<JsonNode>> fromTokens(
        final List<ReferenceToken> tokens)
    {
        final List<TokenResolver<JsonNode>> list = Lists.newArrayList();
        for (final ReferenceToken token: tokens)
            list.add(new JsonNodeResolver(token));
        return list;
    }
}
