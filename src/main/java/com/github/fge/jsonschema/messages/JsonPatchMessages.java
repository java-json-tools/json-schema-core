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

import com.github.fge.jsonschema.exceptions.ExceptionProvider;
import com.github.fge.jsonschema.exceptions.JsonPatchException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.JsonPatchError;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;

public enum JsonPatchMessages
    implements MessageProvider
{
    NULL_INPUT("null inputs are not accepted"),
    NOT_JSON_PATCH("input is not a valid JSON Patch"),
    NO_SUCH_PARENT("parent of node to add does not exist"),
    NOT_AN_INDEX("reference token is not an array index"),
    NO_SUCH_INDEX("no such index in target array"),
    NO_SUCH_PATH("no such path in target JSON document"),
    VALUE_TEST_FAILURE("value differs from expectations"),
    ;

    private final String message;

    JsonPatchMessages(final String message)
    {
        this.message = message;
    }

    @Override
    public ProcessingMessage newMessage()
    {
        return new ProcessingMessage().message(this)
            .setExceptionProvider(new ExceptionProvider()
            {
                @Override
                public ProcessingException doException(
                    final ProcessingMessage message)
                {
                    return new JsonPatchException(message);
                }
            });
    }

    public void checkThat(final boolean condition)
    {
        if (!condition)
            throw new JsonPatchError(newMessage());
    }

    @Override
    public String toString()
    {
        return message;
    }
}
