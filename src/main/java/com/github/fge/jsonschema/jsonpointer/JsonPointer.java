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

    public JsonPointer(final String input)
        throws JsonPointerException
    {
        this(fromString(input));
    }

    private JsonPointer(final List<TokenResolver<JsonNode>> tokenResolvers)
    {
        super(MissingNode.getInstance(), tokenResolvers);
    }

    private static List<TokenResolver<JsonNode>> fromString(final String input)
        throws JsonPointerException
    {
        final List<TokenResolver<JsonNode>> list = Lists.newArrayList();

        for (final ReferenceToken refToken: tokensFromInput(input))
            list.add(new JsonNodeResolver(refToken));

        return ImmutableList.copyOf(list);
    }
}
