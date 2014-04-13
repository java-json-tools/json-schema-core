/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv4;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

/**
 * Syntax checker for draft v4's {@code type} keyword
 */
public final class DraftV4TypeSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final EnumSet<NodeType> ALL_TYPES
        = EnumSet.allOf(NodeType.class);

    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquals.getInstance();

    private static final SyntaxChecker INSTANCE
        = new DraftV4TypeSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private DraftV4TypeSyntaxChecker()
    {
        super("type", NodeType.ARRAY, NodeType.STRING);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);


        if (node.isTextual()) {
            final String s = node.textValue();
            if (NodeType.fromName(s) == null)
                report.error(newMsg(tree, bundle,
                    "common.typeDisallow.primitiveType.unknown")
                    .putArgument("found", s).putArgument("valid", ALL_TYPES));
            return;
        }

        final int size = node.size();

        if (size == 0) {
            report.error(newMsg(tree, bundle, "common.array.empty"));
            return;
        }

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        NodeType type;
        boolean uniqueElements = true;

        for (int index = 0; index <size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueElements = set.add(EQUIVALENCE.wrap(element));
            if (type != NodeType.STRING) {
                report.error(newMsg(tree, bundle,
                    "common.array.element.incorrectType")
                    .putArgument("index", index)
                    .putArgument("expected", NodeType.STRING)
                    .putArgument("found", type));
                continue;
            }
            final String found = element.textValue();
            if (NodeType.fromName(found) == null)
                report.error(newMsg(tree, bundle,
                    "common.typeDisallow.primitiveType.unknown")
                    .put("index", index).putArgument("found", found)
                    .putArgument("valid", ALL_TYPES));
        }

        if (!uniqueElements)
            report.error(newMsg(tree, bundle, "common.array.duplicateElements"));
    }
}
