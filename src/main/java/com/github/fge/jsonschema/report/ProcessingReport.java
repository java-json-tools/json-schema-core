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

import java.util.List;

/**
 * Interface for a processing report
 *
 * <p>This interface is what processors use when reporting messages. The base
 * abstract implementation, {@link AbstractProcessingReport}, provides the
 * necessary mechanics to obey the contract defined by all methods. If you
 * choose to implement it yourself, you <b>must</b> obey this contract as well,
 * otherwise processors may not behave correctly!</p>
 *
 * <p>You will note that this interface extends {@link MessageProvider}: this
 * is because it is perfectly possible for a report to be used as the output
 * of a processor.</p>
 *
 * @see AbstractProcessingReport
 */
public interface ProcessingReport
    extends MessageProvider
{
    /**
     * Set the minimum log level for this report
     *
     * <p>Only messages with a level greater than, or equal to, the level
     * defined here will be processed.</p>
     *
     * @param level the wanted log level
     */
    void setLogLevel(LogLevel level);

    /**
     * Set the exception threshold log level
     *
     * <p>Messages with a level greater than, or equal to, the level defined
     * here will throw a {@link ProcessingException} instead of being logged.
     * </p>
     *
     * @param level the wanted exception threshold
     */
    void setExceptionThreshold(LogLevel level);

    /**
     * Get the current log level threshold of this report
     *
     * <p>This method, and {@link #getExceptionThreshold()}, can be used by
     * other processing reports to reflect the log level and exception threshold
     * of this report.</p>
     *
     * @return the current log level
     */
    LogLevel getLogLevel();

    /**
     * Get the current exception threshold of this report
     *
     * @return the exception threshold
     *
     * @see #getLogLevel()
     */
    LogLevel getExceptionThreshold();

    /**
     * Log one "raw" message
     *
     * <p>In this case, implementations will probably want to inspect the
     * message's log level to take appropriate action. This method is
     * basically intended for log message "injection".</p>
     *
     * @param message the message
     * @throws ProcessingException characteristics of this message grant that
     * an exception be thrown
     * @see ProcessingMessage#getLogLevel()
     * @see ProcessingMessage#asException()
     */
    void log(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a debugging message
     *
     * <p>For this method, and all other similar methods, implementations
     * <b>should</b> set the appropriate log level to the message.</p>
     *
     * @param message the message
     * @throws ProcessingException exception threshold requires that an
     * exception be thrown
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void debug(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log an informational message
     *
     * <p>For this method, and all other similar methods, implementations
     * <b>should</b> set the appropriate log level to the message.</p>
     *
     * @param message the message
     * @throws ProcessingException exception threshold requires that an
     * exception be thrown
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void info(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log a warning message
     *
     * <p>For this method, and all other similar methods, implementations
     * <b>should</b> set the appropriate log level to the message.</p>
     *
     * @param message the message
     * @throws ProcessingException exception threshold requires that an
     * exception be thrown
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void warn(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Log an error message
     *
     * <p>For this method, and all other similar methods, implementations
     * <b>should</b> set the appropriate log level to the message.</p>
     *
     * @param message the message
     * @throws ProcessingException exception threshold requires that an
     * exception be thrown
     * @see ProcessingMessage#setLogLevel(LogLevel)
     */
    void error(ProcessingMessage message)
        throws ProcessingException;

    /**
     * Tell whether this report is a success
     *
     * <p>More often than not, this will return true if all messages logged so
     * far had a level of {@link LogLevel#WARNING} or less.</p>
     *
     * @return true if the processing is considered a success
     */
    boolean isSuccess();

    /**
     * Get a list of collected messages (optional)
     *
     * @return a list of collected messages, if any (may be empty or null)
     */
    List<ProcessingMessage> getMessages();
}
