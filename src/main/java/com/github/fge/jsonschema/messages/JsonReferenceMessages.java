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

package com.github.fge.jsonschema.messages;

/**
 * Messages used for JSON Reference and JSON Pointer anomalous conditions
 *
 * @see com.github.fge.jsonschema.jsonpointer
 * @see com.github.fge.jsonschema.ref
 */
public enum JsonReferenceMessages
{
    /**
     * String is not a valid URI
     */
    INVALID_URI("input is not a valid URI"),
    /**
     * Bad escape sequence in a reference token
     */
    ILLEGAL_ESCAPE("bad escape seqeunce: '~' not followed by a valid token"),
    /**
     * Empty escape sequence in a reference token
     */
    EMPTY_ESCAPE("bad escape sequence: '~' not followed by any token"),
    /**
     * Malformed JSON Pointer: {@code /} not found when expected
     */
    NOT_SLASH("illegal pointer: expected a slash to separate tokens"),
    ;

    private final String message;

    JsonReferenceMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
