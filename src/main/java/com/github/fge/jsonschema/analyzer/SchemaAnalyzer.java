package com.github.fge.jsonschema.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.schema.SchemaSelector;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.walk.DefaultSchemaWalker;
import com.github.fge.jsonschema.walk.SchemaWalker;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.annotations.Beta;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;

@Beta
public final class SchemaAnalyzer
{
    private final LoadingCache<SchemaKey, SchemaAnalysis> cache;

    SchemaAnalyzer(final MessageBundle bundle, final SchemaSelector selector)
    {
        cache = CacheBuilder.newBuilder()
            .build(new SchemaAnalyzerLoader(selector, bundle));
    }

    public ProcessingReport analyze(final SchemaTree tree)
        throws ProcessingException
    {
        final JsonPointer ptr = tree.getPointer();
        SchemaAnalysis analysis;
        try {
            analysis = cache.get(SchemaKey.atRoot(tree));
            if (!analysis.isVisited(ptr))
                analysis = cache.get(SchemaKey.atPointer(tree));
            return analysis.getReport();
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
    }

    private static final class SchemaAnalyzerLoader
        extends CacheLoader<SchemaKey, SchemaAnalysis>
    {
        private final SchemaSelector selector;
        private final MessageBundle bundle;

        private SchemaAnalyzerLoader(final SchemaSelector selector,
            final MessageBundle bundle)
        {
            this.selector = selector;
            this.bundle = bundle;
        }

        @Override
        public SchemaAnalysis load(final SchemaKey key)
            throws ProcessingException
        {
            final SchemaTree tree = key.getTree();
            final SchemaDescriptor descriptor = selector.selectDescriptor(tree);
            final SchemaWalker walker = new DefaultSchemaWalker(descriptor);
            final SchemaSyntaxListener validator
                = new SchemaSyntaxListener(descriptor, bundle);
            final ListProcessingReport report = new ListProcessingReport();
            walker.walk(tree, validator, report);
            return validator.getValue();
        }
    }
}
