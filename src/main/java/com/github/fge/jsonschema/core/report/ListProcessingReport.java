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

package com.github.fge.jsonschema.core.report;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.util.AsJson;
import com.google.common.collect.Iterators;
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * {@link List}-based implementation of a {@link ProcessingReport}
 */
public final class ListProcessingReport
    extends AbstractProcessingReport
    implements AsJson
{
    private static final JsonNodeFactory FACTORY = JacksonUtils.nodeFactory();

    private final List<ProcessingMessage> messages = Lists.newArrayList();

    public ListProcessingReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        super(logLevel, exceptionThreshold);
    }

    public ListProcessingReport(final LogLevel logLevel)
    {
        super(logLevel);
    }

    public ListProcessingReport()
    {
    }

    public ListProcessingReport(final ProcessingReport other)
    {
        this(other.getLogLevel(), other.getExceptionThreshold());
    }

    @Override
    public void log(final LogLevel level, final ProcessingMessage message)
    {
        messages.add(message);
    }

    @Override
    public JsonNode asJson()
    {
        final ArrayNode ret = FACTORY.arrayNode();
        for (final ProcessingMessage message: messages)
            ret.add(message.asJson());
        return ret;
    }

    @Override
    public Iterator<ProcessingMessage> iterator()
    {
        return Iterators.unmodifiableIterator(messages.iterator());
    }

}
