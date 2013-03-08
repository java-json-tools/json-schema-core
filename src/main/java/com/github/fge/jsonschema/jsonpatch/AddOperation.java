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
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.jsonpointer.ReferenceToken;
import com.github.fge.jsonschema.jsonpointer.TokenResolver;

import static com.github.fge.jsonschema.messages.JsonPatchMessages.*;

public final class AddOperation
    extends PathValueOperation
{
    private static final ReferenceToken LAST_ARRAY_ELEMENT
        = ReferenceToken.fromRaw("-");

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
        return parentNode.isArray()
            ? addToArray(split, node)
            : addToObject(split, node);
    }

    private JsonNode addToArray(final SplitPointer split, final JsonNode node)
        throws JsonPatchException
    {
        final JsonNode ret = node.deepCopy();
        final ArrayNode target = (ArrayNode) split.parent.get(ret);
        final TokenResolver<JsonNode> token = split.lastToken;

        if (token.getToken().equals(LAST_ARRAY_ELEMENT)) {
            target.add(value);
            return ret;
        }

        final int size = target.size();
        final int index;
        try {
            index = Integer.parseInt(token.toString());
        } catch (NumberFormatException ignored) {
            throw new JsonPatchException(NOT_AN_INDEX.newMessage()
                .put("token", token.getToken().getRaw()));
        }

        if (index < 0 || index >= size)
            throw new JsonPatchException(NO_SUCH_INDEX.newMessage()
                .put("reminder", "array indices start at 0")
                .put("arraySize", size).put("index", index));

        target.insert(index, value);
        return ret;
    }

    private JsonNode addToObject(final SplitPointer split, final JsonNode node)
    {
        final JsonNode ret = node.deepCopy();
        final ObjectNode target = (ObjectNode) split.parent.get(ret);
        target.put(split.lastToken.getToken().getRaw(), value);
        return ret;
    }

    @Override
    public String toString()
    {
        return "add: " + super.toString();
    }
}
