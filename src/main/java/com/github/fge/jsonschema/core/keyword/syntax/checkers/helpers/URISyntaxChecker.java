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

package com.github.fge.jsonschema.core.keyword.syntax.checkers.helpers;

import com.github.fge.jackson.NodeType;
import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.AbstractSyntaxChecker;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;

/**
 * Helper class to validate the syntax of all keywords whose value must be a URI
 *
 * <p>Note that this checker checks that URIs are normalized.</p>
 */
public final class URISyntaxChecker
    extends AbstractSyntaxChecker
{
    public URISyntaxChecker(final String keyword)
    {
        super(keyword, NodeType.STRING);
    }

    @Override
    protected void checkValue(final Collection<JsonPointer> pointers,
        final MessageBundle bundle, final ProcessingReport report,
        final SchemaTree tree)
        throws ProcessingException
    {
        final String s = getNode(tree).textValue();

        try {
            final URI uri = new URI(s);
            if (!uri.equals(uri.normalize()))
                report.error(newMsg(tree, bundle, "common.uri.notNormalized")
                    .putArgument("value", s));
        } catch (URISyntaxException ignored) {
            report.error(newMsg(tree, bundle, "common.uri.invalid")
                .putArgument("value", s));
        }
    }
}
