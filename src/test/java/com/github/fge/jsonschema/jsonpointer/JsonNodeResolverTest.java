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

    @Test
    public void resolvingNullReturnsNull()
    {
        final JsonNodeResolver resolver
            = new JsonNodeResolver(ReferenceToken.fromRaw("whatever"));

        assertNull(resolver.get(null));
    }

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
