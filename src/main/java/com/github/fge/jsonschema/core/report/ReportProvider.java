/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.report;

import com.github.fge.jsonschema.core.processing.Processor;

/**
 * Report provider interface
 *
 * <p>This interface can be used when wrapping a {@link Processor} into another
 * class which returns a result without providing a report.</p>
 *
 * <p><a href="https://github.com/fge/json-schema-validator">
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
