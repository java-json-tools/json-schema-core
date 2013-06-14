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

package com.github.fge.jsonschema.report;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.google.common.collect.Iterators;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Iterator;

/**
 * A simple forwarding processing report with success/failure state
 *
 * <p>This report forwards all its logged messages to another report and only
 * changes (and retains) its success status to {@code false} if a message is
 * logged at the error level.</p>
 */
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
