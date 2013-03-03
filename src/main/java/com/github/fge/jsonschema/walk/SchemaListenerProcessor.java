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

package com.github.fge.jsonschema.walk;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.processing.Processor;
import com.github.fge.jsonschema.processors.data.SchemaHolder;
import com.github.fge.jsonschema.report.MessageProvider;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.util.ValueHolder;

public final class SchemaListenerProcessor<T extends MessageProvider>
    implements Processor<SchemaHolder, ValueHolder<T>>
{
    private final SchemaWalker walker;
    private final SchemaListenerProvider<T> listenerProvider;

    public SchemaListenerProcessor(final SchemaWalker walker,
        final SchemaListenerProvider<T> listenerProvider)
    {
        this.walker = walker;
        this.listenerProvider = listenerProvider;
    }

    @Override
    public ValueHolder<T> process(final ProcessingReport report,
        final SchemaHolder input)
        throws ProcessingException
    {
        final SchemaListener<T> listener = listenerProvider.newListener();
        walker.walk(listener, report);
        return ValueHolder.hold(listener.getValue());
    }
}
