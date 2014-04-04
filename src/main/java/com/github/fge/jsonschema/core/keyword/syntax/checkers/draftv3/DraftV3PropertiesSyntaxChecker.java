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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.draftv3;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.NodeType;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers.SchemaMapSyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.Maps;

import java.util.Map;
import java.util.SortedMap;

/**
 * Syntax checker for draft v3's {@code properties} keyword
 */
public final class DraftV3PropertiesSyntaxChecker
    extends SchemaMapSyntaxChecker
{
    private static final SyntaxChecker INSTANCE
        = new DraftV3PropertiesSyntaxChecker();

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    private DraftV3PropertiesSyntaxChecker()
    {
        super("properties");
    }

    @Override
    protected void extraChecks(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree)
        throws ProcessingException
    {
        final SortedMap<String, JsonNode> map = Maps.newTreeMap();
        map.putAll(JacksonUtils.asMap(tree.getNode().get(keyword)));

        String member;
        JsonNode required;
        NodeType type;

        for (final Map.Entry<String, JsonNode> entry: map.entrySet()) {
            member = entry.getKey();
            required = entry.getValue().get("required");
            if (required == null)
                continue;
            type = NodeType.getNodeType(required);
            if (type != NodeType.BOOLEAN) {
                report.error(newMsg(tree, bundle,
                    "draftv3.properties.required.incorrectType")
                    .putArgument("property", member).putArgument("found", type)
                    .put("expected", NodeType.BOOLEAN));
            }
        }
    }
}
