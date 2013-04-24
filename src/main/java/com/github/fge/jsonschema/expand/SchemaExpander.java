package com.github.fge.jsonschema.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.ReferenceToken;
import com.github.fge.jackson.jsonpointer.TokenResolver;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.SchemaListener;
import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.List;

public final class SchemaExpander
    implements SchemaListener<SchemaTree>
{
    private final JsonRef baseRef;
    private final ObjectNode baseNode;

    JsonPointer parent = null;
    ReferenceToken token = null;
    ObjectNode currentNode;

    public SchemaExpander(final SchemaTree tree)
    {
        baseRef = tree.getLoadingRef();
        baseNode = tree.getBaseNode().deepCopy();
        currentNode = baseNode;
    }

    @Override
    public void onTreeChange(final SchemaTree oldTree,
        final SchemaTree newTree)
        throws ProcessingException
    {
        currentNode.put(token.getRaw(), newTree.getNode().deepCopy());
    }

    @Override
    public void onWalk(final SchemaTree tree)
        throws ProcessingException
    {
    }

    @Override
    public void onEnter(final JsonPointer pointer)
        throws ProcessingException
    {
        if (pointer.isEmpty())
            return;
        final ArrayList<TokenResolver<JsonNode>> list
            = Lists.newArrayList(pointer);
        final int size = list.size();
        parent = buildPointer(list.subList(0, size - 1));
        token = list.get(size - 1).getToken();
        currentNode = (ObjectNode) parent.get(baseNode);
    }

    @Override
    public void onExit(final JsonPointer pointer)
        throws ProcessingException
    {
    }

    @Override
    public SchemaTree getValue()
    {
        return new CanonicalSchemaTree(baseRef, baseNode);
    }

    private static JsonPointer buildPointer(
        final List<TokenResolver<JsonNode>> list)
    {
        JsonPointer ret = JsonPointer.empty();
        for (final TokenResolver<JsonNode> tokenResolver: list)
            ret = ret.append(tokenResolver.getToken().getRaw());
        return ret;
    }
}
