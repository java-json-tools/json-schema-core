/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.keyword.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.processing.RawProcessor;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Syntax processor
 */
public final class SyntaxProcessor
    extends RawProcessor<SchemaTree, SchemaTree>
{
    private final MessageBundle bundle;
    private final Map<String, SyntaxChecker> checkers;

    public SyntaxProcessor(final MessageBundle bundle,
        final Dictionary<SyntaxChecker> dict)
    {
        super("schema", "schema");
        this.bundle = bundle;
        checkers = dict.entries();
    }

    @Override
    public SchemaTree rawProcess(final ProcessingReport report,
        final SchemaTree input)
        throws ProcessingException
    {
        validate(report, input);
        return input;
    }

    private void validate(final ProcessingReport report, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getNode();
        final NodeType type = NodeType.getNodeType(node);

        /*
         * Barf if not an object, and don't even try to go any further
         */
        if (type != NodeType.OBJECT) {
            report.error(newMsg(tree, "core.notASchema")
                .putArgument("found", type));
            return;
        }

        /*
         * Grab all checkers and object member names. Retain in checkers only
         * existing keywords, and remove from the member names set what is in
         * the checkers' key set: if non empty, some keywords are missing,
         * report them.
         */
        final Map<String, SyntaxChecker> map = Maps.newTreeMap();
        map.putAll(checkers);

        final Set<String> fields = Sets.newHashSet(node.fieldNames());
        map.keySet().retainAll(fields);
        fields.removeAll(map.keySet());

        if (!fields.isEmpty())
            report.warn(newMsg(tree, "core.unknownKeywords")
                .putArgument("ignored", Ordering.natural().sortedCopy(fields)));

        /*
         * Now, check syntax of each keyword, and collect pointers for further
         * analysis.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final SyntaxChecker checker: map.values())
            checker.checkSyntax(pointers, bundle, report, tree);

        /*
         * Operate on these pointers.
         */
        for (final JsonPointer pointer: pointers)
            validate(report, tree.append(pointer));
    }

    private ProcessingMessage newMsg(final SchemaTree tree, final String key)
    {
        return new ProcessingMessage().put("schema", tree)
            .put("domain", "syntax").setMessage(bundle.getMessage(key));

    }

    @Override
    public String toString()
    {
        return "syntax checker";
    }
}
