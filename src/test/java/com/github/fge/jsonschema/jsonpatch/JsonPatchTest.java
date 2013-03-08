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

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.exceptions.unchecked.JsonPatchError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.util.JacksonUtils;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.JsonPatchMessages.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class JsonPatchTest
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();
    private JsonPatchOperation op1;
    private JsonPatchOperation op2;

    @BeforeMethod
    public void init()
    {
        op1 = mock(JsonPatchOperation.class);
        op2 = mock(JsonPatchOperation.class);
    }

    @Test
    public void nullInputsDuringBuildAreRejected()
        throws JsonPatchException
    {
        try {
            JsonPatch.fromJson(null);
            fail("No exception thrown!!");
        } catch (JsonPatchError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_INPUT);
        }
    }
}
