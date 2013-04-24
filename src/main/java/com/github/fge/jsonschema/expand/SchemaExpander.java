package com.github.fge.jsonschema.expand;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jackson.jsonpointer.TokenResolver;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.SchemaListener;
import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import java.util.Deque;
import java.util.List;

public final class SchemaExpander
    implements SchemaListener<SchemaTree>
{
    private final JsonRef baseRef;
    private JsonNode baseNode;

    private JsonPointer path = JsonPointer.empty();
    private final Deque<JsonPointer> paths = Queues.newArrayDeque();

    public SchemaExpander(final SchemaTree tree)
    {
        baseRef = tree.getLoadingRef();
        baseNode = tree.getBaseNode().deepCopy();
    }

    @Override
    public void onTreeChange(final SchemaTree oldTree,
        final SchemaTree newTree)
        throws ProcessingException
    {
        final JsonNode newNode = newTree.getNode().deepCopy();
        if (path.isEmpty()) {
            baseNode = newNode;
            return;
        }

        final JsonPointer parent = getParent(path);
        final String token = getLastToken(path);
        final JsonNode parentNode = parent.get(baseNode);
        final NodeType type = NodeType.getNodeType(parentNode);
        switch (type) {
            case OBJECT:
                ((ObjectNode) parentNode).put(token, newNode);
                break;
            case ARRAY:
                ((ArrayNode) parentNode).set(Integer.parseInt(token), newNode);
                break;
            default:
                throw new IllegalStateException("was expecting an object or" +
                    " an array");
        }
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
        final JsonPointer ptr = path.append(pointer);
        paths.push(path);
        path = ptr;
    }

    @Override
    public void onExit(final JsonPointer pointer)
        throws ProcessingException
    {
        path = paths.pop();
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

    private static JsonPointer getParent(final JsonPointer ptr)
    {
        final List<TokenResolver<JsonNode>> list = Lists.newArrayList(ptr);
        final int size = list.size();

        return buildPointer(list.subList(0, size - 1));
    }

    private static String getLastToken(final JsonPointer ptr)
    {
        final List<TokenResolver<JsonNode>> list = Lists.newArrayList(ptr);
        final int size = list.size();

        return list.get(size - 1).getToken().getRaw();
    }
}
