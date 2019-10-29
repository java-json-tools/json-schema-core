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

package com.github.fge.jsonschema.core.keyword.syntax.checkers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.SyntaxProcessor;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.google.common.annotations.VisibleForTesting;

import java.util.Collection;
import java.util.EnumSet;

/**
 * Interface for a syntax checker
 *
 * <p>This is a syntax checker for one keyword. As some keywords contain
 * subschemas, a list of {@link JsonPointer} is also passed as an argument so
 * that the checker can instruct the syntax processor to also check these paths
 * after it is done.</p>
 *
 * <p>Note that when a syntax checker is called on a schema, it is guaranteed
 * that the keyword exists in the schema.</p>
 *
 * @see SyntaxProcessor
 */
public interface SyntaxChecker
{
    // FIXME: I should get rid of that -- it is used in only one place.
    EnumSet<NodeType> getValidTypes();

    /**
     * Check the syntax for this keyword
     *
     * @param pointers the list of JSON Pointers to fill (see description)
     * @param  bundle the message bundle to use
     * @param report the processing report to use
     * @param tree the schema
     * @throws ProcessingException an error is detected, and the report is
     * configured to throw an exception on error.
     */
    void checkSyntax(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException;
}
