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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.util.JsonLoader;

import java.io.IOException;
import java.net.URI;

/**
 * JSON Schema versions
 *
 * <p>Members of this enum offer two informations about JSON Schemas:</p>
 *
 * <ul>
 *     <li>their location (what is used in {@code $schema}),</li>
 *     <li>the meta schema (as a {@link JsonNode}.</li>
 * </ul>
 */
public enum SchemaVersion
{
    /**
     * Draft v4 (default version)
     */
    DRAFTV4("http://json-schema.org/draft-04/schema#", "/draftv4/schema"),
    /**
     * Draft v3
     */
    DRAFTV3("http://json-schema.org/draft-03/schema#", "/draftv3/schema"),
    ;

    private final URI location;
    private final JsonNode schema;

    SchemaVersion(final String uri, final String resource)
    {
        try {
            location = URI.create(uri);
            schema = JsonLoader.fromResource(resource);
        } catch (IOException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    /**
     * Return the value of {@code $schema} as a {@link JsonRef}
     *
     * @return the JSON Reference for that schema version
     */
    public URI getLocation()
    {
        return location;
    }

    /**
     * Return the meta schema as JSON
     *
     * <p>Note: since {@link JsonNode} is mutable, this method returns a copy.
     * </p>
     *
     * @return the meta schema
     * @see JsonNode#deepCopy()
     */
    public JsonNode getSchema()
    {
        return schema.deepCopy();
    }
}
