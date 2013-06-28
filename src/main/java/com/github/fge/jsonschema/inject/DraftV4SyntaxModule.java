package com.github.fge.jsonschema.inject;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.report.ConsoleProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.google.inject.Guice;
import com.google.inject.Injector;

import java.io.IOException;

public class DraftV4SyntaxModule
    extends AbstractSyntaxModule
{
    public DraftV4SyntaxModule()
    {
        messages.appendBundle(BASE_BUNDLE);
        // TODO: replace if/when Guice is considered viable (watch out tests!)
        checkers.addAll(DraftV4SyntaxCheckerDictionary.get());
    }

    public static void main(final String... args)
        throws IOException, ProcessingException
    {
        final ProcessingReport report = new ConsoleProcessingReport();
        final Injector injector
            = Guice.createInjector(new DraftV4SyntaxModule());

        final SyntaxProcessor processor
            = injector.getInstance(SyntaxProcessor.class);

        final JsonNode node = JsonLoader.fromString("{\"type\": 1}");
        processor.rawProcess(report, new CanonicalSchemaTree(node));
    }
}
