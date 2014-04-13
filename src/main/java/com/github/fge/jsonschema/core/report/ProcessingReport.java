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

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

/**
 * Interface for a processing report
 *
 * <p>While you can implement this interface yourself, it is recommended that
 * you extend {@link AbstractProcessingReport} instead.</p>
 */
public interface ProcessingReport
    extends Iterable<ProcessingMessage>
{
    /**
     * Get the log level of this report
     *
     * <p>Any message with a log level greater than, or equal to, the result of
     * this method is logged.</p>
     *
     * @return the log level
     */
    LogLevel getLogLevel();

    /**
     * Get the exception threshold of this report
     *
     * <p>Any message with a log level greater than, or equal to, the result of
     * this method raises a {@link ProcessingException} or any subclass.</p>
     *
     * @return the exception threshold
     * @see ProcessingMessage#asException()
     */
    LogLevel getExceptionThreshold();

    /**
     * Log a message with a level of {@link LogLevel#DEBUG}
     *
     * <p>It is the responsibility of the implementation to set the log level
     * of the message appropriately.</p>
     *
     * @param message the message
     * @throws ProcessingException the level of this message grants that an
     * exception is thrown instead
     * @see #getExceptionThreshold()
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void debug(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a message with a level of {@link LogLevel#INFO}
     *
     * <p>It is the responsibility of the implementation to set the log level
     * of the message appropriately.</p>
     *
     * @param message the message
     * @throws ProcessingException the level of this message grants that an
     * exception is thrown instead
     * @see #getExceptionThreshold()
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void info(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a message with a level of {@link LogLevel#WARNING}
     *
     * <p>It is the responsibility of the implementation to set the log level
     * of the message appropriately.</p>
     *
     * @param message the message
     * @throws ProcessingException the level of this message grants that an
     * exception is thrown instead
     * @see #getExceptionThreshold()
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void warn(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a message with a level of {@link LogLevel#ERROR}
     *
     * <p>It is the responsibility of the implementation to set the log level
     * of the message appropriately.</p>
     *
     * @param message the message
     * @throws ProcessingException the level of this message grants that an
     * exception is thrown instead
     * @see #getExceptionThreshold()
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void error(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a message with a level of {@link LogLevel#FATAL}
     *
     * <p>It is the responsibility of the implementation to set the log level
     * of the message appropriately.</p>
     *
     * @param message the message
     * @throws ProcessingException the level of this message grants that an
     * exception is thrown instead
     * @see #getExceptionThreshold()
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void fatal(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Tell whether the report is a success
     *
     * <p>A report is considered successful if no messages with a level of
     * {@link LogLevel#ERROR} or higher have been logged.</p>
     *
     * @return a boolean
     */
    boolean isSuccess();

    /**
     * Merge another report into this report
     *
     * @param other the other report
     * @throws ProcessingException a message in the other report has a level
     * granting that an exception be thrown
     */
    void mergeWith(ProcessingReport other)
        throws ProcessingException;
}
