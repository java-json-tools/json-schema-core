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

import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.report.ProcessingReport;
import com.github.fge.jsonschema.core.report.SimpleExceptionProvider;

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
