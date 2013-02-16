package com.github.fge.jsonschema.jsonpointer;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
        throws JsonPointerException
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
        throws URISyntaxException, JsonPointerException
    {
        final URI uri = new URI(input);
        final JsonPointer pointer = new JsonPointer(uri.getFragment());

        assertEquals(pointer.get(document), expected);
    }
}
