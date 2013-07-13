package com.github.fge.jsonschema.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.keyword.SchemaDescriptor;
import com.github.fge.jsonschema.keyword.SchemaSelector;
import com.github.fge.jsonschema.report.ListProcessingReport;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.InjectedWith;
import com.github.fge.jsonschema.walk.DefaultSchemaWalker;
import com.github.fge.jsonschema.walk.SchemaWalker;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.annotations.Beta;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.inject.Inject;
import java.util.concurrent.ExecutionException;

import static com.github.fge.jsonschema.util.InjectedWith.*;

@Beta
@InjectedWith({
    @Injection(MessageBundle.class),
    @Injection(SchemaSelector.class)
})
public final class SchemaAnalyzer
{
    private final LoadingCache<SchemaKey, SchemaAnalysis> cache;

    @Inject
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
            final SyntaxValidator validator
                = new SyntaxValidator(descriptor, bundle);
            final ListProcessingReport report = new ListProcessingReport();
            walker.walk(tree, validator, report);
            return validator.getValue();
        }
    }
}
