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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.InvalidSchemaException;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.util.Collection;

/**
 * Helper class to validate the syntax of keywords having either a schema or
 * schema array as a value
 */
public abstract class SchemaOrSchemaArraySyntaxChecker
    extends AbstractSyntaxChecker
{
    protected SchemaOrSchemaArraySyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.ARRAY, NodeType.OBJECT);
    }

    @Override
    protected final void checkValue(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        collectPointers(pointers, tree);
        extraChecks(report, bundle, tree);
    }

    /**
     * Perform extra check on the keyword
     *
     * @param report the report to use
     * @param bundle the message bundle to use
     * @param tree the schema
     * @throws InvalidSchemaException schema is invalid
     */
    protected abstract void extraChecks(final ProcessingReport report,
        final MessageBundle bundle, final SchemaTree tree)
        throws ProcessingException;

    private void collectPointers(final Collection<JsonPointer> pointers,
        final SchemaTree tree)
    {
        final JsonNode node = getNode(tree);
        if (node.isObject()) {
            pointers.add(JsonPointer.of(keyword));
            return;
        }

        for (int index = 0; index < node.size(); index++)
            pointers.add(JsonPointer.of(keyword, index));
    }
}
