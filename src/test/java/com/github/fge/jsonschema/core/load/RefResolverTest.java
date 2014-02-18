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

package com.github.fge.jsonschema.core.load;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.util.ValueHolder;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class RefResolverTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final RefResolver processor = new RefResolver(null);
    private final ProcessingReport report = mock(ProcessingReport.class);

    @Test
    public void refLoopsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        try {
            processor.process(report, holder);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("refProcessing.refLoop", "#"));
        }
    }

    @Test
    public void danglingRefsAreReported()
    {
        final ObjectNode node = JacksonUtils.nodeFactory().objectNode();
        node.put("$ref", "#/a");

        final SchemaTree tree = new CanonicalSchemaTree(node);
        final ValueHolder<SchemaTree> holder = ValueHolder.hold("schema", tree);

        try {
            processor.process(report, holder);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("refProcessing.danglingRef", "#/a"));
        }
    }
}
