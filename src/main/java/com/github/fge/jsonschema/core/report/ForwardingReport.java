/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.report;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.google.common.collect.Iterators;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

/**
 * A simple forwarding processing report with success/failure state
 *
 * <p>This report forwards all its logged messages to another report and only
 * changes (and retains) its success status to {@code false} if a message is
 * logged at the error level.</p>
 *
 * @deprecated does not work properly; will be removed in 1.1.7.
 */
@Deprecated
@NotThreadSafe
public final class ForwardingReport
    implements ProcessingReport
{
    private final ProcessingReport report;
    private boolean success = true;

    /**
     * Constructor
     *
     * @param report the report to forward logged messages to
     */
    public ForwardingReport(final ProcessingReport report)
    {
        this.report = report;
    }

    @Override
    public LogLevel getLogLevel()
    {
        return LogLevel.DEBUG;
    }

    @Override
    public LogLevel getExceptionThreshold()
    {
        return LogLevel.NONE;
    }

    @Override
    public void debug(final ProcessingMessage message)
        throws ProcessingException
    {
        report.debug(message);
    }

    @Override
    public void info(final ProcessingMessage message)
        throws ProcessingException
    {
        report.info(message);
    }

    @Override
    public void warn(final ProcessingMessage message)
        throws ProcessingException
    {
        report.warn(message);
    }

    @Override
    public void error(final ProcessingMessage message)
        throws ProcessingException
    {
        success = false;
        report.error(message);
    }

    @Override
    public void fatal(final ProcessingMessage message)
        throws ProcessingException
    {
        success = false;
        report.fatal(message);
    }

    @Override
    public boolean isSuccess()
    {
        return success;
    }

    @Override
    public void mergeWith(final ProcessingReport other)
        throws ProcessingException
    {
        success = success && other.isSuccess();
    }

    @Override
    public Iterator<ProcessingMessage> iterator()
    {
        return Iterators.emptyIterator();
    }
}
