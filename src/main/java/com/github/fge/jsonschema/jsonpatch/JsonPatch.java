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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.util.JacksonUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;

import java.io.IOException;
import java.util.List;

import static com.github.fge.jsonschema.messages.JsonPatchMessages.*;

public final class JsonPatch
{
    private final List<JsonPatchOperation> operations;

    @JsonCreator
    @VisibleForTesting
    JsonPatch(final List<JsonPatchOperation> operations)
    {
        this.operations = ImmutableList.copyOf(operations);
    }

    public static JsonPatch fromJson(final JsonNode node)
        throws JsonPatchException
    {
        NULL_INPUT.checkThat(node != null);
        try {
            return JacksonUtils.getReader().withType(JsonPatch.class)
                .readValue(node);
        } catch (IOException e) {
            throw new JsonPatchException(NOT_JSON_PATCH.newMessage(), e);
        }
    }
    public JsonNode apply(final JsonNode node)
        throws JsonPatchException
    {
        NULL_INPUT.checkThat(node != null);
        JsonNode ret = node;
        for (final JsonPatchOperation operation: operations)
            ret = operation.apply(ret);

        return ret;
    }

    @Override
    public String toString()
    {
        return operations.toString();
    }
}
