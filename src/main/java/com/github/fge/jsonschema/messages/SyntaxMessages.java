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
 * Messages used by syntax checking
 */
public enum SyntaxMessages
{
    NOT_A_SCHEMA("JSON value is not a JSON Schema: not an object"),
    UNKNOWN_KEYWORDS("unknown keyword(s) found; ignored"),
    INCORRECT_TYPE("value has incorrect type"),
    INTEGER_TOO_LARGE("integer value is too large"),
    INTEGER_IS_NEGATIVE("integer value must be positive"),
    EXCLUSIVEMINIMUM("exclusiveMinimum must be paired with minimum"),
    EXCLUSIVEMAXIMUM("exclusiveMaximum must be paired with maximum"),
    INVALID_REGEX_MEMBER_NAME("member name is not a valid ECMA 262 regular expression"),
    INVALID_REGEX_VALUE("value is not a valid ECMA 262 regular expression"),
    INVALID_URI("string is not a valid URI"),
    URI_NOT_NORMALIZED("URI is not normalized"),
    ELEMENTS_NOT_UNIQUE("array must not contain duplicate elements"),
    EMPTY_ARRAY("array must have at least one element"),
    ILLEGAL_DIVISOR("divisor must be strictly greater than zero"),
    INCORRECT_ELEMENT_TYPE("array element has incorrect type"),
    INCORRECT_DEPENDENCY_VALUE("dependency value has incorrect type"),
    INCORRECT_PRIMITIVE_TYPE("incorrect primitive type"),
    DRAFTV3_PROPERTIES_REQUIRED("\"required\" attribute has wrong type"),
    EXTENDS_EMPTY_ARRAY("no elements in \"extends\" array"),
    INVALID_SCHEMA("invalid schema, cannot continue"),
    HS_MEDIA_INVALID_ENCODING_TYPE("invalid type for media binary encoding"),
    HS_MEDIA_INVALID_ENCODING("invalid binary encoding for media"),
    HS_MEDIA_INVALID_TYPE_TYPE("invalid primitive type for media MIME type"),
    HS_MEDIA_INVALID_TYPE("invalid MIME type for media"),
    HS_LINKS_LDO_BAD_TYPE("invalid type for LDO object"),
    HS_LINKS_LDO_MISSING_REQ("missing required property(ies) in LDO"),
    HS_LINKS_LDO_REL_WRONG_TYPE("incorrect type for rel"),
    HS_LINKS_LDO_HREF_WRONG_TYPE("incorrect type for href"),
    HS_LINKS_LDO_HREF_ILLEGAL("href is not a URI template"),
    HS_LINKS_LDO_TITLE_WRONG_TYPE("incorrect type for title"),
    HS_LINKS_LDO_MEDIATYPE_WRONG_TYPE("incorrect type for mediaType"),
    HS_LINKS_LDO_MEDIATYPE_ILLEGAL("mediaType is not a MIME type"),
    HS_LINKS_LDO_METHOD_WRONG_TYPE("incorrect type for method"),
    ;

    private final String message;

    SyntaxMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
