/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.misc.analyzer;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.schema.SchemaSelector;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.walk.DefaultSchemaWalker;
import com.github.fge.jsonschema.core.walk.SchemaWalker;
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
