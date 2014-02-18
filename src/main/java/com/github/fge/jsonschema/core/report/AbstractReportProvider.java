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

package com.github.fge.jsonschema.core.report;

/**
 * Base implementation of a {@link ReportProvider}
 *
 * <p>This base implementation takes a log level and exception threshold, and
 * generates a new processing report according to these parameters.</p>
 */
public abstract class AbstractReportProvider
    implements ReportProvider
{
    protected final LogLevel logLevel;
    protected final LogLevel exceptionThreshold;

    /**
     * Protected constructor
     *
     * @param logLevel the log level to use when generating a new report
     * @param exceptionThreshold the exception threshold to use
     */
    protected AbstractReportProvider(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        this.logLevel = logLevel;
        this.exceptionThreshold = exceptionThreshold;
    }
}
