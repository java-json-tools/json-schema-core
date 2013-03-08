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

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;

import static com.fasterxml.jackson.annotation.JsonSubTypes.*;
import static com.fasterxml.jackson.annotation.JsonTypeInfo.*;

@JsonTypeInfo(use = Id.NAME, include = As.PROPERTY, property = "op")

@JsonSubTypes({
    @Type(name = "add", value = AddOperation.class),
    @Type(name = "copy", value = CopyOperation.class),
    @Type(name = "move", value = MoveOperation.class),
    @Type(name = "remove", value = RemoveOperation.class),
    @Type(name = "replace", value = ReplaceOperation.class),
    @Type(name = "test", value = TestOperation.class)
})

public abstract class JsonPatchOperation
{
    /*
     * Note: no need for a custom deserializer, Jackson will try and find a
     * constructor with a single string argument and use it
     */
    protected final JsonPointer path;

    protected JsonPatchOperation(final JsonPointer path)
    {
        this.path = path;
    }

    public abstract JsonNode apply(final JsonNode source)
        throws JsonPatchException;

    @Override
    public String toString()
    {
        return "path = \"" + path + '"';
    }
}
