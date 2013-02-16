package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.NodeType;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.*;

public final class JsonNodeResolverTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    @DataProvider
    public Iterator<Object[]> nonContainers()
    {
        return SampleNodeProvider.getSamplesExcept(NodeType.ARRAY,
            NodeType.OBJECT);
    }

    @Test(dataProvider = "nonContainers")
    public void resolvingNonContainerNodeReturnsNull(final JsonNode node)
    {
        final JsonNodeResolver resolver
            = new JsonNodeResolver(ReferenceToken.fromRaw("whatever"));

        assertNull(resolver.get(node));
    }

    @Test
    public void resolvingObjectMembersWorks()
    {
        final JsonNodeResolver resolver
            = new JsonNodeResolver(ReferenceToken.fromRaw("a"));
        final JsonNode target = FACTORY.textNode("b");

        ObjectNode node;

        node = FACTORY.objectNode();
        node.put("a", target);

        final JsonNode resolved = resolver.get(node);
        assertEquals(resolved, target);

        node = FACTORY.objectNode();
        node.put("b", target);

        assertNull(resolver.get(node));
    }

    @Test
    public void resolvingArrayIndicesWorks()
    {
        final JsonNodeResolver resolver
            = new JsonNodeResolver(ReferenceToken.fromInt(1));

        final JsonNode target = FACTORY.textNode("b");
        final ArrayNode node = FACTORY.arrayNode();

        node.add(target);
        assertNull(resolver.get(node));

        node.add(target);
        assertEquals(target, resolver.get(node));
    }

    @Test
    public void invalidIndicesYieldNull()
    {
        final JsonNode target = FACTORY.textNode("b");
        final ArrayNode node = FACTORY.arrayNode();

        node.add(target);

        ReferenceToken refToken;
        JsonNodeResolver resolver;

        refToken = ReferenceToken.fromInt(-1);
        resolver = new JsonNodeResolver(refToken);
        assertNull(resolver.get(node));

        refToken = ReferenceToken.fromRaw("00");
        resolver = new JsonNodeResolver(refToken);
        assertNull(resolver.get(node));
    }
}
