package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.base.Stopwatch;
import com.google.common.io.Closeables;
import org.testng.reporters.Files;

import java.io.IOException;
import java.io.InputStream;

public final class ReadingTest
{
    private static final int TRIES = 10000;

    public static void main(final String... args)
        throws IOException
    {
        final InputStream in
            = ReadingTest.class.getResourceAsStream("/draftv4/schema");
        final String input = Files.streamToString(in);
        Closeables.closeQuietly(in);

        final ObjectReader mapper = JacksonUtils.getReader();

        final Stopwatch stopwatch = new Stopwatch();

        // JsonNode

        final JsonNode templateJsonNode = mapper.readTree(input);

        JsonNode jsonNode;
        stopwatch.start();
        for (int i = 0; i < TRIES; i++)
            jsonNode = mapper.readTree(input);
        stopwatch.stop();

        System.out.println("JsonNode read: " + stopwatch);

        stopwatch.reset();

        jsonNode = mapper.readTree(input);

        stopwatch.start();
        for (int i = 0; i < TRIES; i++)
            templateJsonNode.equals(jsonNode);

        stopwatch.stop();

        System.out.println("JsonNode equals: " + stopwatch);
    }
}
