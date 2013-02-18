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

package com.github.fge.jsonschema.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingMessage;

public abstract class ValueHolder<T>
    implements MessageProvider
{
    protected final String name;
    protected final T value;

    protected ValueHolder(final String name, final T value)
    {
        this.name = name;
        this.value = value;
    }

    protected abstract JsonNode valueAsJson();

    public final T getValue()
    {
        return value;
    }

    @Override
    public final ProcessingMessage newMessage()
    {
        return new ProcessingMessage().put(name, valueAsJson());
    }
}
