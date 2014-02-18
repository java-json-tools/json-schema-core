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

import com.github.fge.jsonschema.core.processing.Processor;

/**
 * Report provider interface
 *
 * <p>This interface can be used when wrapping a {@link Processor} into another
 * class which returns a result without providing a report.</p>
 *
 * <p><a href="https://github.com/fge/json-schema-validator>
 * json-schema-validator</a> uses this, for instance, in its main validator
 * class.</p>
 */
public interface ReportProvider
{
    /**
     * Generate a new report
     *
     * @return a new report
     */
    ProcessingReport newReport();

    /**
     * Generate a new report with an adapted log level and the same exception
     * threshold
     *
     * @param logLevel the new log level
     * @return a new report
     */
    ProcessingReport newReport(final LogLevel logLevel);

    /**
     * Generate a new report with an adapted log level and exception threshold
     *
     * @param logLevel the new log level
     * @param exceptionThreshold the new exception threshold
     * @return a new report
     */
    ProcessingReport newReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold);
}
