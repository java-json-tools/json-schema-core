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

import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.report.ProcessingReport;
import com.github.fge.jsonschema.report.SimpleExceptionProvider;

/**
 * An exception provider for a {@link ProcessingMessage}
 *
 * <p>The main use of this interface is in processing messages themselves:
 * {@link ProcessingReport}, for instance, uses a message's {@link
 * ProcessingMessage#asException()} method to throw the exception associated
 * with that message. The latter method just returns the result of {@link
 * #doException(ProcessingMessage)} with {@code this} as an argument.</p>
 *
 * @see SimpleExceptionProvider
 * @see ProcessingMessage
 * @see ProcessingReport
 */
public interface ExceptionProvider
{
    /**
     * Return an exception associated with a message
     *
     * @param message the message
     * @return the appropriate exception
     */
    ProcessingException doException(final ProcessingMessage message);
}
