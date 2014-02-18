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

package com.github.fge.jsonschema.core.analyzer;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.report.ListProcessingReport;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.walk.SchemaListener;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SchemaSyntaxListener
    implements SchemaListener<SchemaAnalysis>
{
    private final Set<JsonPointer> visited = Sets.newHashSet();
    private final ListProcessingReport messages = new ListProcessingReport();

    private final Set<String> supported;
    private final Map<String, SyntaxChecker> checkers;
    private final MessageBundle bundle;

    public SchemaSyntaxListener(final SchemaDescriptor descriptor,
        final MessageBundle bundle)
    {
        supported = descriptor.getSupportedKeywords();
        checkers = Maps.newTreeMap();
        checkers.putAll(descriptor.getSyntaxCheckers());
        this.bundle = bundle;
    }

    @Override
    public void enteringPath(final JsonPointer path,
        final ProcessingReport report)
        throws ProcessingException
    {
        visited.add(path);
    }

    @Override
    public void visiting(final SchemaTree schemaTree,
        final ProcessingReport report)
        throws ProcessingException
    {
        final JsonNode node = schemaTree.getNode();

        if (!node.isObject()) {
            final ProcessingMessage message = new ProcessingMessage()
                .setMessage(bundle.getMessage("core.notASchema"))
                .putArgument("found", NodeType.getNodeType(node))
                .put("schema", schemaTree);
            messages.error(message);
            return;
        }

        final Set<String> fields = Sets.newHashSet(node.fieldNames());
        final Sets.SetView<String> unknown = Sets.difference(fields, supported);

        if (!unknown.isEmpty()) {
            final ProcessingMessage message = new ProcessingMessage()
                .put("domain", "syntax")
                .setMessage(bundle.getMessage("core.unknownKeywords"))
                .putArgument("ignored", Ordering.natural().sortedCopy(unknown));
            messages.warn(message);
        }

        final List<JsonPointer> list = Lists.newArrayList();
        for (final Map.Entry<String, SyntaxChecker> entry:
            checkers.entrySet()) {
            if (!fields.contains(entry.getKey()))
                continue;
            entry.getValue().checkSyntax(list, bundle, messages, schemaTree);
        }
    }

    @Override
    public void exitingPath(final JsonPointer path,
        final ProcessingReport report)
        throws ProcessingException
    {
    }

    @Override
    public SchemaAnalysis getValue()
    {
        return new SchemaAnalysis(visited, messages);
    }
}
