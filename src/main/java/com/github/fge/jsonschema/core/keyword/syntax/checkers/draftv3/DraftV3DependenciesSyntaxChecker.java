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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.DependenciesSyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.base.Equivalence;
import com.google.common.collect.Sets;

import java.util.EnumSet;
import java.util.Set;

/**
 * Syntax checker for draft v3's {@code dependencies} keyword
 */
public final class DraftV3DependenciesSyntaxChecker
    extends DependenciesSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new DraftV3DependenciesSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private DraftV3DependenciesSyntaxChecker()
    {
        super(NodeType.ARRAY, NodeType.STRING);
    }

    @Override
    protected void checkDependency(final ProcessingReport report,
        final MessageBundle bundle, final String name, final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree).get(name);
        NodeType type;

        type = NodeType.getNodeType(node);

        if (type == NodeType.STRING)
            return;

        if (type != NodeType.ARRAY) {
            report.error(newMsg(tree, bundle,
                "common.dependencies.value.incorrectType")
                .putArgument("property", name)
                .putArgument("expected", dependencyTypes)
                .putArgument("found", type));
            return;
        }

        final int size = node.size();

        /*
         * Yep, in draft v3, nothing prevents a dependency array from being
         * empty! This is stupid, so at least warn the user.
         */
        if (size == 0) {
            report.warn(newMsg(tree, bundle, "common.array.empty")
                .put("property", name));
            return;
        }

        final Set<Equivalence.Wrapper<JsonNode>> set = Sets.newHashSet();

        JsonNode element;
        boolean uniqueElements = true;

        for (int index = 0; index < size; index++) {
            element = node.get(index);
            type = NodeType.getNodeType(element);
            uniqueElements = set.add(EQUIVALENCE.wrap(element));
            if (type == NodeType.STRING)
                continue;
            report.error(newMsg(tree, bundle,
                "common.array.element.incorrectType")
                .put("property", name).putArgument("index", index)
                .putArgument("expected", EnumSet.of(NodeType.STRING))
                .putArgument("found", type));
        }

        /*
         * Similarly, there is nothing preventing duplicates. Equally stupid,
         * so warn the user.
         */
        if (!uniqueElements)
            report.warn(newMsg(tree, bundle, "common.array.duplicateElements")
                .put("property", name));
    }
}
