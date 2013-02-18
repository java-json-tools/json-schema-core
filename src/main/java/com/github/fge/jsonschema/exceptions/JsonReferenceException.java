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

package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.ProcessingMessage;

/**
 * Exception associated with all JSON Reference exceptions
 *
 * <p>This exception is used by {@link JsonRef} and {@link JsonPointer} to
 * signify errors.</p>
 *
 * <p>Note however that this is not the exception thrown if inputs are null:
 * in this case a {@link JsonReferenceError} (unchecked) is thrown.</p>
 *
 * @see JsonRef
 * @see JsonPointer
 */
public final class JsonReferenceException
    extends ProcessingException
{
    public JsonReferenceException(final ProcessingMessage message)
    {
        super(message);
    }

    public JsonReferenceException(final ProcessingMessage message,
        final Throwable e)
    {
        super(message, e);
    }
}
