package com.github.fge.jsonschema.inject;

import com.github.fge.jsonschema.analyzer.SchemaAnalyzer;
import com.google.inject.Injector;

public final class JsonSchemaFactory
{
    private final SchemaAnalyzer analyzer;

    public static JsonSchemaFactory byDefault()
    {
        return newBuilder().build();
    }

    public static JsonSchemaFactoryBuilder newBuilder()
    {
        return new JsonSchemaFactoryBuilder();
    }

    JsonSchemaFactory(final Injector injector)
    {
        analyzer = injector.getInstance(SchemaAnalyzer.class);
    }
}

