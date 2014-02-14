/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;
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
