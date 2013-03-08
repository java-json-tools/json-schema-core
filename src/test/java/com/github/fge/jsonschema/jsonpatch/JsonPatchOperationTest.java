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

package com.github.fge.jsonschema.jsonpatch;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.messages.JsonPatchMessages;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.github.fge.jsonschema.util.JsonLoader;
import com.github.fge.jsonschema.util.equivalence.JsonSchemaEquivalence;
import com.google.common.base.Equivalence;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.assertMessage;
import static org.testng.Assert.*;

public abstract class JsonPatchOperationTest
{
    private static final Equivalence<JsonNode> EQUIVALENCE
        = JsonSchemaEquivalence.getInstance();

    private final JsonNode errors;
    private final JsonNode ops;
    private final Class<? extends JsonPatchOperation> opClass;
    private final ObjectReader reader;

    protected JsonPatchOperationTest(final String prefix,
        final Class<? extends JsonPatchOperation> opClass)
        throws IOException
    {
        final String resource = "/jsonpatch/" + prefix + ".json";
        final JsonNode node = JsonLoader.fromResource(resource);
        errors = node.get("errors");
        ops = node.get("ops");
        this.opClass = opClass;
        reader = JacksonUtils.getReader().withType(opClass);
    }

    @DataProvider
    public final Iterator<Object[]> getErrors()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node: errors)
            list.add(new Object[]{
                node.get("patch"),
                node.get("node"),
                JsonPatchMessages.valueOf(node.get("message").textValue()),
                node.get("msgData")
            });

        return list.iterator();
    }

    @Test(dataProvider = "getErrors")
    public final void errorsAreCorrectlyReported(final JsonNode patch,
        final JsonNode node, final JsonPatchMessages msg,
        final ObjectNode msgData)
        throws IOException
    {
        final JsonPatchOperation op = reader.readValue(patch);

        try {
            op.apply(node);
            fail("No exception thrown!!");
        } catch (JsonPatchException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasLevel(LogLevel.FATAL)
                .hasMessage(msg).hasContents(msgData);
        }
    }

    @DataProvider
    public final Iterator<Object[]> getOps()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final JsonNode node: ops)
            list.add(new Object[]{
                node.get("patch"),
                node.get("node"),
                node.get("expected")
            });

        return list.iterator();
    }

    @Test(dataProvider = "getOps")
    public final void operationsYieldExpectedResults(final JsonNode patch,
        final JsonNode node, final JsonNode expected)
        throws IOException, JsonPatchException
    {
        final JsonPatchOperation op = reader.readValue(patch);
        final JsonNode actual = op.apply(node);

        assertNotSame(actual, expected, "operation did not copy the node");
        assertTrue(EQUIVALENCE.equivalent(actual, expected),
            "unexpected output for operation");
    }
}

