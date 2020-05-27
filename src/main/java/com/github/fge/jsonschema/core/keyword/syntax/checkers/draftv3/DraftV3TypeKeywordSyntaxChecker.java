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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNumEquivalence;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Set;

import static com.github.fge.jackson.NodeType.*;

/**
 * Helper class to validate the syntax of draft v3's {@code type} and {@code
 * disallow}
 */
public final class DraftV3TypeKeywordSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final String ANY = "any";
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonNumEquivalence.getInstance();

    public DraftV3TypeKeywordSyntaxChecker(final String keyword)
    {
        super(keyword, STRING, ARRAY);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = tree.getNode().get(keyword);

        if (node.isTextual()) {
            final String found = node.textValue();
            if (!typeIsValid(found))
                report.error(newMsg(tree, bundle,
                    "common.typeDisallow.primitiveType.unknown")
                    .putArgument("found", found)
                    .putArgument("valid", EnumSet.allOf(NodeType.class)));
            return;
        }

        final int size = node.size();
        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        NodeType type;
        boolean uniqueItems = true;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueItems = set.add(EQUIVALENCE.wrap(element));
            if (type == OBJECT) {
                pointers.add(JsonPointer.of(keyword, index));
                continue;
            }
            if (type != STRING) {
                report.error(newMsg(tree, bundle,
                    "common.array.element.incorrectType")
                    .putArgument("index", index)
                    .putArgument("expected", EnumSet.of(OBJECT, STRING))
                    .putArgument("found", type));
                continue;
            }
            if (!typeIsValid(element.textValue()))
                report.error(newMsg(tree, bundle,
                    "common.typeDisallow.primitiveType.unknown")
                    .put("index", index)
                    .putArgument("found", element.textValue())
                    .putArgument("valid", EnumSet.allOf(NodeType.class)));
        }

        if (!uniqueItems)
            report.error(newMsg(tree, bundle, "common.array.duplicateElements"));
    }

    private static boolean typeIsValid(final String s)
    {
        return ANY.equals(s) || NodeType.fromName(s) != null;

    }
}
