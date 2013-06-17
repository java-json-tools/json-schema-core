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
import com.github.fge.jsonschema.processing.RawProcessor;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.ValueHolder;

/**
 * Schema walking processor
 *
 * <p>This processor requires that you provide both a {@link
 * SchemaWalkerFactory}, to build a schema walker out of a {@link SchemaTree},
 * and a {@link SchemaListenerFactory}, to build a schema listener.</p>
 *
 * <p>When processing an input, a new walker and a new listener will be built,
 * and the schema will be processed. The return value of the listener will then
 * be wrapped into a {@link ValueHolder}.</p>
 *
 * @param <T> the value type produced by the listeners
 */
public final class SchemaWalkerProcessor<T>
    extends RawProcessor<SchemaTree, T>
{
    private final SchemaWalkerFactory walkerFactory;
    private final SchemaListenerFactory<T> listenerFactory;

    /**
     * Constructor
     *
     * @param walkerFactory a {@link SchemaWalker} provider
     * @param listenerFactory a {@link SchemaListener} provider
     */
    public SchemaWalkerProcessor(final SchemaWalkerFactory walkerFactory,
        final SchemaListenerFactory<T> listenerFactory)
    {
        super("schema", "value");
        this.walkerFactory = walkerFactory;
        this.listenerFactory = listenerFactory;
    }

    @Override
    public T rawProcess(final ProcessingReport report, final SchemaTree input)
        throws ProcessingException
    {
        final SchemaWalker walker = walkerFactory.newWalker(input);
        final SchemaListener<T> listener = listenerFactory.newListener();
        walker.walk(listener, report);
        return listener.getValue();
    }
}
