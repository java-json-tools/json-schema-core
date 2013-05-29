/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.syntax;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.messages.CoreMessageBundles;
import com.github.fge.jsonschema.messages.MessageBundle;
import com.github.fge.jsonschema.processing.RawProcessor;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.tree.SchemaTree;
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
    private static final MessageBundle BUNDLE = CoreMessageBundles.SYNTAX;

    private final Map<String, SyntaxChecker> checkers;

    public SyntaxProcessor(final Dictionary<SyntaxChecker> dict)
    {
        super("schema", "schema");
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
            final ProcessingMessage msg = newMsg(tree)
                .message(BUNDLE.getString("notASchema")).put("found", type);
            report.error(msg);
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

        final Set<String> fieldNames = Sets.newHashSet(node.fieldNames());
        map.keySet().retainAll(fieldNames);
        fieldNames.removeAll(map.keySet());

        if (!fieldNames.isEmpty())
            report.warn(newMsg(tree)
                .message(BUNDLE.getString("unknownKeywords"))
                .put("ignored", Ordering.natural().sortedCopy(fieldNames)));

        /*
         * Now, check syntax of each keyword, and collect pointers for further
         * analysis.
         */
        final List<JsonPointer> pointers = Lists.newArrayList();
        for (final SyntaxChecker checker: map.values())
            checker.checkSyntax(pointers, report, tree);

        /*
         * Operate on these pointers.
         */
        for (final JsonPointer pointer: pointers)
            validate(report, tree.append(pointer));
    }

    private static ProcessingMessage newMsg(final SchemaTree tree)
    {
        return new ProcessingMessage().put("schema", tree)
            .put("domain", "syntax");
    }

    @Override
    public String toString()
    {
        return "syntax checker";
    }
}
