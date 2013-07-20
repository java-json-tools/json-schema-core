package com.github.fge.jsonschema.inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.analyzer.SchemaAnalyzer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

public final class JsonSchemaFactory
{
    private final Injector injector;

    public static JsonSchemaFactory byDefault()
    {
        return newBuilder().build();
    }

    public static JsonSchemaFactoryBuilder newBuilder()
    {
        return new JsonSchemaFactoryBuilder();
    }

    JsonSchemaFactory(final JsonSchemaFactoryBuilder builder)
    {
        injector = Guice.createInjector(
            builder.schemaReaderModule,
            builder.schemaSelectorModule,
            builder.uriTranslatorModule,
            builder.syntaxMessageBundleModule
        );
    }

    public SchemaAnalyzer getAnalyzer()
    {
        return injector.getInstance(SchemaAnalyzer.class);
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final JsonNode node = JsonLoader.fromString("12");
        final JsonSchemaFactory factory = JsonSchemaFactory.byDefault();
        final SchemaAnalyzer analyzer = factory.getAnalyzer();
        final ProcessingReport report
            = analyzer.analyze(new CanonicalSchemaTree(node));

        System.out.println(report);
    }
}

