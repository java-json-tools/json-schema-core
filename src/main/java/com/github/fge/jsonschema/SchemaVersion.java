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

package com.github.fge.jsonschema;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonLoader;
import com.github.fge.jsonschema.core.ref.JsonRef;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * JSON Schema versions
 *
 * <p>Members of this enum offer two informations about JSON Schemas:</p>
 *
 * <ul>
 *     <li>their location (what is used in {@code $schema}),</li>
 *     <li>the meta schema (as a {@link JsonNode}).</li>
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
    /**
     * Draft v4 hyperschema
     */
    DRAFTV4_HYPERSCHEMA("http://json-schema.org/draft-04/hyper-schema#",
        "/draftv4/hyper-schema"),
    ;

    private final URI location;
    @SuppressWarnings("ImmutableEnumChecker")
    private final JsonNode schema;

    SchemaVersion(final String uri, final String resource)
    {
        try {
            location = URI.create(uri);
            final URL url = SchemaVersion.class.getResource(resource);
            schema = JsonLoader.fromURL(url);
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
