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

package com.github.fge.jsonschema.exceptions;

import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.jsonschema.report.ProcessingMessage;
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
