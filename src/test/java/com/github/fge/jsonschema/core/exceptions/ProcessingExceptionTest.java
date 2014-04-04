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

package com.github.fge.jsonschema.core.exceptions;

import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class ProcessingExceptionTest
{
    private static final String FOO = "foo";

    @Test
    public void thrownProcessingMessagesHaveLevelFatal()
    {
        final ProcessingMessage message = new ProcessingMessage();
        new ProcessingException(message);
        assertMessage(message).hasLevel(LogLevel.FATAL);
    }

    @Test
    public void processingExceptionMessageIsSameAsProcessingMessage()
    {
        final ProcessingMessage message = new ProcessingMessage()
            .setMessage(FOO);
        final ProcessingException exception = new ProcessingException(message);
        assertEquals(exception.getMessage(), message.toString());
    }

    @Test
    public void innerExceptionClassAndMessageAreReported()
    {
        final Exception inner = new Foo(FOO);
        final ProcessingException exception
            = new ProcessingException("", inner);
        final ProcessingMessage message = exception.getProcessingMessage();
        assertMessage(message).hasField("exceptionClass", Foo.class.getName())
            .hasField("exceptionMessage", inner.getMessage());
    }

    private static class Foo
        extends Exception
    {
        private Foo(final String message)
        {
            super(message);
        }
    }
}
