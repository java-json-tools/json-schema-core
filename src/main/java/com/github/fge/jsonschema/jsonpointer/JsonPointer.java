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
