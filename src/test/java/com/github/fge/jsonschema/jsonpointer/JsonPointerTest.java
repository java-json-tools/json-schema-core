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
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.SampleNodeProvider;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.github.fge.jsonschema.jsonpointer.JsonPointerMessages.NULL_INPUT;
import static org.testng.Assert.*;

public final class JsonPointerTest
{
    private final JsonNode testData;
    private final JsonNode document;

    public JsonPointerTest()
        throws IOException
    {
        testData = JsonLoader.fromResource("/jsonpointer/jsonpointer.json");
        document = testData.get("document");
    }

    @Test
    public void cannotAppendNullPointer()
    {
        final JsonPointer foo = null;
        try {
            JsonPointer.empty().append(foo);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), NULL_INPUT);
        }
    }

    @DataProvider
    public Iterator<Object[]> rawPointers()
    {
        final List<Object[]> list = Lists.newArrayList();
        final JsonNode testNode = testData.get("pointers");
        final Map<String, JsonNode> map = JacksonUtils.asMap(testNode);

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            list.add(new Object[] { entry.getKey(), entry.getValue() });

        return list.iterator();
    }

    @Test(dataProvider = "rawPointers")
    public void rawPointerResolvingWorks(final String input,
        final JsonNode expected)
        throws JsonReferenceException
    {
        final JsonPointer pointer = new JsonPointer(input);

        assertEquals(pointer.get(document), expected);
    }

    @DataProvider
    public Iterator<Object[]> uriPointers()
    {
        final List<Object[]> list = Lists.newArrayList();
        final JsonNode testNode = testData.get("uris");
        final Map<String, JsonNode> map = JacksonUtils.asMap(testNode);

        for (final Map.Entry<String, JsonNode> entry: map.entrySet())
            list.add(new Object[] { entry.getKey(), entry.getValue() });

        return list.iterator();
    }

    @Test(dataProvider = "uriPointers")
    public void uriPointerResolvingWorks(final String input,
        final JsonNode expected)
        throws URISyntaxException, JsonReferenceException
    {
        final URI uri = new URI(input);
        final JsonPointer pointer = new JsonPointer(uri.getFragment());

        assertEquals(pointer.get(document), expected);
    }

    @Test
    public void appendingRawTokensToAPointerWorks()
        throws JsonReferenceException
    {
        final JsonPointer ptr = new JsonPointer("/foo/bar");
        final String raw = "/0~";
        final JsonPointer expected = new JsonPointer("/foo/bar/~10~0");

        assertEquals(ptr.append(raw), expected);
    }

    @Test
    public void appendingIndicesToAPointerWorks()
        throws JsonReferenceException
    {
        final JsonPointer ptr = new JsonPointer("/foo/bar/");
        final int index = 33;
        final JsonPointer expected = new JsonPointer("/foo/bar//33");

        assertEquals(ptr.append(index), expected);
    }

    @Test
    public void appendingOnePointerToAnotherWorks()
        throws JsonReferenceException
    {
        final JsonPointer ptr = new JsonPointer("/a/b");
        final JsonPointer appended = new JsonPointer("/c/d");
        final JsonPointer expected = new JsonPointer("/a/b/c/d");

        assertEquals(ptr.append(appended), expected);
    }

    @DataProvider
    public Iterator<Object[]> allInstanceTypes()
    {
        return SampleNodeProvider.getSamples(EnumSet.allOf(NodeType.class));
    }

    @Test(dataProvider = "allInstanceTypes")
    public void emptyPointerAlwaysReturnsTheSameInstance(final JsonNode node)
    {
        assertEquals(JsonPointer.empty().get(node), node);
    }

    @Test
    public void staticConstructionFromTokensWorks()
        throws JsonReferenceException
    {
        JsonPointer ptr1, ptr2;

        ptr1 = JsonPointer.of("a", "b");
        ptr2 = new JsonPointer("/a/b");
        assertEquals(ptr1, ptr2);

        ptr1 = JsonPointer.of("", "/", "~");
        ptr2 = new JsonPointer("//~1/~0");
        assertEquals(ptr1, ptr2);

        ptr1 = JsonPointer.of(1, "xx", 0);
        ptr2 = new JsonPointer("/1/xx/0");
        assertEquals(ptr1, ptr2);

        ptr1 = JsonPointer.of("");
        ptr2 = new JsonPointer("/");
        assertEquals(ptr1, ptr2);
    }
}
