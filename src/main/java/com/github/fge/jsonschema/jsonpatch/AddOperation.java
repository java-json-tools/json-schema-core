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
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;

import static com.github.fge.jsonschema.messages.JsonPatchMessages.*;

public final class AddOperation
    extends PathValueOperation
{
    @JsonCreator
    public AddOperation(@JsonProperty("path") final JsonPointer path,
        @JsonProperty("value") final JsonNode value)
    {
        super(path, value);
    }

    @Override
    public JsonNode apply(final JsonNode node)
        throws JsonPatchException
    {
        if (path.isEmpty())
            return value;

        /*
         * Check the parent node: it must at the very least exist for the add
         * operation to work
         */
        final SplitPointer split = new SplitPointer(path);
        final JsonNode parentNode = split.parent.path(node);
        if (parentNode.isMissingNode())
            throw new JsonPatchException(NO_SUCH_PARENT.newMessage()
                .put("node", node).put("path", path.toString()));
        return doAdd(split, node);
    }

    private JsonNode doAdd(final SplitPointer split, final JsonNode node)
        throws JsonPatchException
    {
        final JsonNode targetNode = path.path(node);
        if (targetNode.isValueNode())
            throw new JsonPatchException(CANNOT_ADD_TO_VALUE.newMessage()
                .put("node", node).put("path", path.toString())
                .put("resolved", targetNode));
        return node;
    }

    @Override
    public String toString()
    {
        return "add: " + super.toString();
    }
}
