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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.hyperschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.collect.ImmutableSet;
import com.google.common.net.MediaType;

import java.util.Collection;
import java.util.Set;

public final class MediaSyntaxChecker
    extends AbstractSyntaxChecker
{
    private static final String BINARY_ENCODING_FIELDNAME = "binaryEncoding";
    private static final String TYPE_FIELDNAME = "type";

    // FIXME: INCOMPLETE: excludes "x-token" and "ietf-token"
    private static final Set<String> BINARY_ENCODINGS = ImmutableSet.of(
        "7bit", "8bit", "binary", "quoted-printable", "base64"
    );

    private static final SyntaxChecker INSTANCE = new MediaSyntaxChecker();

    private MediaSyntaxChecker()
    {
        super("media", NodeType.OBJECT);
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
        JsonNode subNode;
        NodeType type;
        String value;

        subNode = node.path(BINARY_ENCODING_FIELDNAME);
        if (!subNode.isMissingNode()) {
            type = NodeType.getNodeType(subNode);
            value = subNode.textValue();
            if (value == null)
                report.error(newMsg(tree, bundle,
                    "draftv4.media.binaryEncoding.incorrectType")
                    .put("expected", NodeType.STRING)
                    .putArgument("found", type));
            else if (!BINARY_ENCODINGS.contains(value.toLowerCase()))
                report.error(newMsg(tree, bundle,
                    "draftv4.media.binaryEncoding.invalid")
                    .putArgument("value", value)
                    .putArgument("valid", BINARY_ENCODINGS));
        }

        subNode = node.path(TYPE_FIELDNAME);
        if (subNode.isMissingNode())
            return;
        type = NodeType.getNodeType(subNode);
        if (type != NodeType.STRING) {
            report.error(newMsg(tree, bundle,
                "draftv4.media.type.incorrectType")
                .put("expected", NodeType.STRING).putArgument("found", type));
            return;
        }
        value = subNode.textValue();
        try {
            MediaType.parse(value);
        } catch (IllegalArgumentException ignored) {
            report.error(newMsg(tree, bundle, "draftv4.media.type.notMediaType")
                .putArgument("value", value));
        }
    }
}
