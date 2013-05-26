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

import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Messages used by the configuration process
 */
public enum JsonReferenceErrors
{
    NULL_URI("provided URI cannot be null"),
    INVALID_URI("input is not a valid URI"),
    REF_NOT_ABSOLUTE("input is not an absolute JSON Reference"),
    NULL_INPUT("string input cannot be null"),
    ;

    private final String message;

    JsonReferenceErrors(final String message)
    {
        this.message = message;
    }

    public void checkThat(final boolean condition)
    {
        if (!condition)
            throw new JsonReferenceError(asMessage());
    }

    public ProcessingMessage asMessage()
    {
        return new ProcessingMessage().message(this);
    }

    @Override
    public String toString()
    {
        return message;
    }
}
