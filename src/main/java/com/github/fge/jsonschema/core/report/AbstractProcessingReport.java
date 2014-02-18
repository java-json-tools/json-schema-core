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
import com.google.common.collect.Lists;

import java.util.Iterator;
import java.util.List;

/**
 * Base implementation of a processing report
 *
 * <p>This abstract class implements all the logic of a processing report. The
 * only method you need to implement is {@link #log(LogLevel,
 * ProcessingMessage)}, which will implement the actual logging of the message.
 * When entering this method, the message's log level will already have been set
 * correctly.</p>
 */
public abstract class AbstractProcessingReport
    implements ProcessingReport
{
    /**
     * The highest log level seen so far
     */
    private LogLevel currentLevel = LogLevel.DEBUG;

    /**
     * The log threshold
     */
    private final LogLevel logLevel;

    /**
     * The exception threshold
     */
    private final LogLevel exceptionThreshold;

    /**
     * Main constructor
     *
     * @param logLevel the log threshold for this report
     * @param exceptionThreshold the exception threshold for this report
     */
    protected AbstractProcessingReport(final LogLevel logLevel,
        final LogLevel exceptionThreshold)
    {
        this.logLevel = logLevel;
        this.exceptionThreshold = exceptionThreshold;
    }

    /**
     * Alternate constructor
     *
     * <p>This constructor calls {@link #AbstractProcessingReport(LogLevel,
     * LogLevel)} with {@link LogLevel#FATAL} as the second argument.</p>
     *
     * @param logLevel the log threshold
     */
    protected AbstractProcessingReport(final LogLevel logLevel)
    {
        this(logLevel, LogLevel.FATAL);
    }

    /**
     * Alternate constructor
     *
     * <p>This constructor calls {@link #AbstractProcessingReport(LogLevel,
     * LogLevel)} with {@link LogLevel#INFO} as the first argument and {@link
     * LogLevel#FATAL} as the second argument.</p>
     */
    protected AbstractProcessingReport()
    {
        this(LogLevel.INFO, LogLevel.FATAL);
    }

    @Override
    public final LogLevel getLogLevel()
    {
        return logLevel;
    }

    @Override
    public final LogLevel getExceptionThreshold()
    {
        return exceptionThreshold;
    }

    @Override
    public final void debug(final ProcessingMessage message)
        throws ProcessingException
    {
        dispatch(message.setLogLevel(LogLevel.DEBUG));
    }

    @Override
    public final void info(final ProcessingMessage message)
        throws ProcessingException
    {
        dispatch(message.setLogLevel(LogLevel.INFO));
    }

    @Override
    public final void warn(final ProcessingMessage message)
        throws ProcessingException
    {
        dispatch(message.setLogLevel(LogLevel.WARNING));
    }

    @Override
    public final void error(final ProcessingMessage message)
        throws ProcessingException
    {
        dispatch(message.setLogLevel(LogLevel.ERROR));
    }

    @Override
    public final void fatal(final ProcessingMessage message)
        throws ProcessingException
    {
        dispatch(message.setLogLevel(LogLevel.FATAL));
    }

    @Override
    public final boolean isSuccess()
    {
        return currentLevel.compareTo(LogLevel.ERROR) < 0;
    }

    /**
     * The only method to be implemented when extending this class
     *
     * <p>Note that the message's log level will have been correctly set. The
     * log level is passed as an argument for convenience.</p>
     *
     * @param level the level of the message
     * @param message the message itself
     */
    public abstract void log(final LogLevel level,
        final ProcessingMessage message);

    /**
     * Main dispatch method
     *
     * <p>All messages logged go through this method. According to the report
     * configuration, the message will either be ignored, logged or raise an
     * exception.</p>
     *
     * @param message the message to log
     * @throws ProcessingException the message's level and report configuration
     * require that an exception be thrown
     */
    protected final void dispatch(final ProcessingMessage message)
        throws ProcessingException
    {
        final LogLevel level = message.getLogLevel();

        if (level.compareTo(exceptionThreshold) >= 0)
            throw message.asException();
        if (level.compareTo(currentLevel) > 0)
            currentLevel = level;
        if (level.compareTo(logLevel) >= 0)
            log(level, message);
    }

    @Override
    public Iterator<ProcessingMessage> iterator()
    {
        return Iterators.emptyIterator();
    }

    @Override
    public final void mergeWith(final ProcessingReport other)
        throws ProcessingException
    {
        /*
         * The other report may have no messages, and as such the successful
         * status won't be overriden: we have to do that instead
         */
        if (!other.isSuccess() && currentLevel.compareTo(LogLevel.ERROR) < 0)
            currentLevel = LogLevel.ERROR;
        for (final ProcessingMessage message: other)
            dispatch(message);
    }

    @Override
    public final String toString()
    {
        final StringBuilder sb
            = new StringBuilder(getClass().getCanonicalName()).append(": ")
                .append(isSuccess() ? "success" : "failure").append('\n');
        final List<ProcessingMessage> messages = Lists.newArrayList(this);
        if (!messages.isEmpty()) {
            sb.append("--- BEGIN MESSAGES ---\n");
            for (final ProcessingMessage message: messages)
                sb.append(message);
            sb.append("---  END MESSAGES  ---\n");
        }
        return sb.toString();
    }
}
