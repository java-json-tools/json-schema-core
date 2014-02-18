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
