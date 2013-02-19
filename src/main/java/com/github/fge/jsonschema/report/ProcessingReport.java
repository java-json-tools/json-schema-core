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
 * Base implementation of a processing report
 *
 * <p>This abstract class implements all the logic of a processing report. The
 * only method you need to implement is {@link
 * #log(LogLevel, ProcessingMessage)}, which will implement the actual logging
 * of the message. When entering this method, the message's log level will
 * already have been set correctly.</p>
 */
public abstract class ProcessingReport
{
    protected LogLevel currentLevel = LogLevel.DEBUG;
    private LogLevel logLevel = LogLevel.INFO;
    private LogLevel exceptionThreshold = LogLevel.FATAL;

    protected ProcessingReport()
    {
    }

    public final void setLogLevel(final LogLevel level)
    {
        logLevel = level;
    }

    public final void setExceptionThreshold(final LogLevel level)
    {
        exceptionThreshold = level;
    }

    public final LogLevel getLogLevel()
    {
        return logLevel;
    }

    public final LogLevel getExceptionThreshold()
    {
        return exceptionThreshold;
    }

    public final void debug(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.DEBUG));
    }

    public final void info(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.INFO));
    }

    public final void warn(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.WARNING));
    }

    public final void error(final ProcessingMessage message)
        throws ProcessingException
    {
        log(message.setLogLevel(LogLevel.ERROR));
    }

    public final boolean isSuccess()
    {
        return currentLevel.compareTo(LogLevel.ERROR) < 0;
    }

    public abstract void log(final LogLevel level,
        final ProcessingMessage message);

    private void log(final ProcessingMessage message)
        throws ProcessingException
    {
        final LogLevel level = message.getLogLevel();

        if (level.compareTo(exceptionThreshold) >= 0)
            throw message.asException();
        if (level.compareTo(currentLevel) > 0)
            currentLevel = level;
        if (level.compareTo(logLevel) >= 0)
            log(level, message.setLogLevel(level));
    }

    public final ProcessingMessage newMessage()
    {
        return new ProcessingMessage();
    }

    public abstract List<ProcessingMessage> getMessages();
}
