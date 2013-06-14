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
