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

import com.github.fge.jsonschema.exceptions.unchecked.ProcessingConfigurationError;
import com.github.fge.jsonschema.report.AbstractProcessingReport;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.SimpleExceptionProvider;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;

/**
 * An exception provider for a {@link ProcessingMessage}
 *
 * <p>The main use of this interface is in processing messages themselves:
 * {@link AbstractProcessingReport}, for instance, uses a message's {@link
 * ProcessingMessage#asException()} method to throw the exception associated
 * with that message. The latter method just returns the result of {@link
 * #doException(ProcessingMessage)} with {@code this} as an argument.</p>
 *
 * @see SimpleExceptionProvider
 * @see ProcessingMessage
 * @see AbstractProcessingReport
 */
public final class ExceptionProvider
{
    private static final ProcessingMessage MESSAGE = new ProcessingMessage();

    private final Constructor<? extends ProcessingException> constructor;

    public static ExceptionProvider forClass(
        final Class<? extends ProcessingException> c)
    {
        return new ExceptionProvider(c);
    }

    private ExceptionProvider(final Class<? extends ProcessingException> c)
        throws ProcessingConfigurationError
    {
        try {
            constructor = c.getConstructor(ProcessingMessage.class);
            doException(MESSAGE);
        } catch (NoSuchMethodException e) {
            throw new ProcessingConfigurationError(new ProcessingMessage()
                .message(NO_EXCEPTION_CONSTRUCTOR), e);
        }
    }

    /**
     * Return an exception associated with a message
     *
     * @param message the message
     * @return the appropriate exception
     */
    public ProcessingException doException(final ProcessingMessage message)
    {
        try {
            return constructor.newInstance(message);
        } catch (InstantiationException e) {
            throw new ProcessingConfigurationError(new ProcessingMessage()
                .message(EXCEPTION_INSTANTIATION_ERROR), e);
        } catch (IllegalAccessException e) {
            throw new ProcessingConfigurationError(new ProcessingMessage()
                .message(EXCEPTION_INSTANTIATION_ERROR), e);
        } catch (InvocationTargetException e) {
            throw new ProcessingConfigurationError(new ProcessingMessage()
                .message(EXCEPTION_INSTANTIATION_ERROR), e);
        }
    }
}
