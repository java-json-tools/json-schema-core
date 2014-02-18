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
 * A processing report which logs absolutely nothing
 *
 * <p>Use this class if all you are interested in is the processing status.</p>
 */
public final class DevNullProcessingReport
    extends AbstractProcessingReport
{
    public DevNullProcessingReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        super(logLevel, exceptionThreshold);
    }

    public DevNullProcessingReport(final LogLevel logLevel)
    {
        super(logLevel);
    }

    public DevNullProcessingReport()
    {
    }

    @Override
    public void log(final LogLevel level, final ProcessingMessage message)
    {
    }
}
