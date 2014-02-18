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

package com.github.fge.jsonschema.core.exceptions;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;

/**
 * Generic processing exception
 *
 * <p>Internally, this class always keeps its information in a {@link
 * ProcessingMessage}. Note that all messages see their log level set to
 * {@link LogLevel#FATAL}.</p>
 *
 * @see ProcessingMessage
 * @see LogLevel
 */
public class ProcessingException
    extends Exception
{
    /**
     * The internal message
     */
    private final ProcessingMessage processingMessage;

    public ProcessingException()
    {
        this(new ProcessingMessage().setLogLevel(LogLevel.FATAL));
    }

    public ProcessingException(final String message)
    {
        this(new ProcessingMessage().setMessage(message)
            .setLogLevel(LogLevel.FATAL));
    }

    public ProcessingException(final ProcessingMessage message)
    {
        processingMessage = message.setLogLevel(LogLevel.FATAL);
    }

    public ProcessingException(final String message, final Throwable e)
    {
        processingMessage = new ProcessingMessage().setLogLevel(LogLevel.FATAL)
            .setMessage(message).put("exceptionClass", e.getClass().getName())
            .put("exceptionMessage", e.getMessage());
    }

    public ProcessingException(final ProcessingMessage message,
        final Throwable e)
    {
        processingMessage = message.setLogLevel(LogLevel.FATAL)
            .put("exceptionClass", e.getClass().getName())
            .put("exceptionMessage", e.getMessage());
    }

    @Override
    public final String getMessage()
    {
        return processingMessage.toString();
    }

    public final String getShortMessage()
    {
        return processingMessage.getMessage();
    }

    public final ProcessingMessage getProcessingMessage()
    {
        return processingMessage;
    }
}
