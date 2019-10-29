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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.hyperschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.uritemplate.URITemplate;
import com.github.fge.uritemplate.URITemplateParseException;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.net.MediaType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Syntax checker for draft v4 hyperschema's {@code links} keyword
 */
public final class LinksSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final ImmutableList<String> REQUIRED_LDO_PROPERTIES
        = ImmutableList.of("href", "rel");

    private static final SyntaxChecker INSTANCE = new LinksSyntaxChecker();

    private LinksSyntaxChecker()
    {
        super("links", NodeType.ARRAY);
    }

    public static SyntaxChecker getInstance()
    {
        return INSTANCE;
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree);
        final int size = node.size();

        JsonNode ldo;
        NodeType type;
        Set<String> set;
        List<String> list;

        for (int index = 0; index < size; index++) {
            ldo = getNode(tree).get(index);
            type = NodeType.getNodeType(ldo);
            if (type != NodeType.OBJECT) {
                report.error(LDOMsg(tree, bundle, "draftv4.ldo.incorrectType", index)
                    .put("expected", NodeType.OBJECT)
                    .putArgument("found", type));
                continue;
            }
            set = Sets.newHashSet(ldo.fieldNames());
            list = Lists.newArrayList(REQUIRED_LDO_PROPERTIES);
            list.removeAll(set);
            if (!list.isEmpty()) {
                final ProcessingMessage msg = LDOMsg(tree, bundle,
                    "draftv4.ldo.missingRequired", index);
                report.error(msg.put("required", REQUIRED_LDO_PROPERTIES)
                    .putArgument("missing", list));
                continue;
            }
            if (ldo.has("schema"))
                pointers.add(JsonPointer.of(keyword, index, "schema"));
            if (ldo.has("targetSchema"))
                pointers.add(JsonPointer.of(keyword, index, "targetSchema"));
            checkLDO(report, bundle, tree, index);
        }
    }

    private void checkLDO(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree, final int index)
        throws ProcessingException
    {
        final JsonNode ldo = getNode(tree).get(index);

        String value;

        checkLDOProperty(report, bundle, tree, index, "rel", NodeType.STRING,
            "draftv4.ldo.rel.incorrectType");

        if (checkLDOProperty(report, bundle, tree, index, "href",
            NodeType.STRING, "draftv4.ldo.href.incorrectType")) {
            value = ldo.get("href").textValue();
            try {
                new URITemplate(value);
            } catch (URITemplateParseException ignored) {
                report.error(LDOMsg(tree, bundle,
                    "draftv4.ldo.href.notURITemplate",
                    index).putArgument("value", value));
            }
        }

        checkLDOProperty(report, bundle, tree, index, "title", NodeType.STRING,
            "draftv4.ldo.title.incorrectType");

        if (checkLDOProperty(report, bundle, tree, index, "mediaType",
            NodeType.STRING, "draftv4.ldo.mediaType.incorrectType")) {
            value = ldo.get("mediaType").textValue();
            try {
                MediaType.parse(value);
            } catch (IllegalArgumentException ignored) {
                report.error(LDOMsg(tree, bundle,
                    "draftv4.ldo.mediaType.notMediaType",
                    index).putArgument("value", value));
            }
        }

        checkLDOProperty(report, bundle, tree, index, "method",
            NodeType.STRING, "draftv4.ldo.method.incorrectType");

        if (checkLDOProperty(report, bundle, tree, index, "encType",
            NodeType.STRING, "draftv4.ldo.enctype.incorrectType")) {
            value = ldo.get("encType").textValue();
            try {
                MediaType.parse(value);
            } catch (IllegalArgumentException ignored) {
                report.error(LDOMsg(tree, bundle,
                    "draftv4.ldo.enctype.notMediaType",
                    index).putArgument("value", value));
            }
        }
    }

    private ProcessingMessage LDOMsg(final SchemaTree tree,
        final MessageBundle bundle, final String key, final int index)
    {
        return newMsg(tree, bundle, key).put("index", index);
    }

    private boolean checkLDOProperty(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree, final int index,
        final String name, final NodeType expected, final String key)
        throws ProcessingException
    {
        final JsonNode node = getNode(tree).get(index).get(name);

        if (node == null)
            return false;

        final NodeType type = NodeType.getNodeType(node);

        if (type == expected)
            return true;

        report.error(LDOMsg(tree, bundle, key, index).put("expected", expected)
                .putArgument("found", type));
        return false;
    }
}
