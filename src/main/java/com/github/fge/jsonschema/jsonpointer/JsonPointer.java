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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import java.util.List;

public final class JsonPointer
    extends TreePointer<JsonNode>
{
    private static final JsonPointer EMPTY
        = new JsonPointer(ImmutableList.<TokenResolver<JsonNode>>of());

    public static JsonPointer empty()
    {
        return EMPTY;
    }

    public static JsonPointer of(final Object first, final Object... other)
    {
        final List<ReferenceToken> tokens = Lists.newArrayList();

        tokens.add(ReferenceToken.fromRaw(first.toString()));

        for (final Object o: other)
            tokens.add(ReferenceToken.fromRaw(o.toString()));

        return new JsonPointer(fromTokens(tokens));
    }

    public JsonPointer(final String input)
        throws JsonPointerException
    {
        this(fromTokens(tokensFromInput(input)));
    }

    private JsonPointer(final List<TokenResolver<JsonNode>> tokenResolvers)
    {
        super(MissingNode.getInstance(), tokenResolvers);
    }

    public JsonPointer append(final String raw)
    {
        final ReferenceToken refToken = ReferenceToken.fromRaw(raw);
        final JsonNodeResolver resolver = new JsonNodeResolver(refToken);
        final List<TokenResolver<JsonNode>> list
            = Lists.newArrayList(tokenResolvers);
        list.add(resolver);
        return new JsonPointer(list);
    }

    public JsonPointer append(final int index)
    {
        return append(Integer.toString(index));
    }

    public JsonPointer append(final JsonPointer other)
    {
        final List<TokenResolver<JsonNode>> list
            = Lists.newArrayList(tokenResolvers);
        list.addAll(other.tokenResolvers);
        return new JsonPointer(list);
    }

    private static List<TokenResolver<JsonNode>> fromTokens(
        final List<ReferenceToken> tokens)
    {
        final List<TokenResolver<JsonNode>> list = Lists.newArrayList();
        for (final ReferenceToken token: tokens)
            list.add(new JsonNodeResolver(token));
        return list;
    }
}
