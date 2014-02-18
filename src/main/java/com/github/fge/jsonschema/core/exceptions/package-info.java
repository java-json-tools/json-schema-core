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

/**
 * Exceptions and exception provider
 *
 * <p>This package contains checked exceptions raised by the API core. The base
 * exception is {@link
 * com.github.fge.jsonschema.core.exceptions.ProcessingException} and all other
 * exceptions, save for unchecked exceptions, inherit it.</p>
 *
 * <p>The {@link com.github.fge.jsonschema.core.exceptions.ExceptionProvider}
 * interface can be used by your own custom processors to set custom
 * exceptions in messages: {@link
 * com.github.fge.jsonschema.core.report.ProcessingMessage} accepts such a provider
 * and will then return the appropriate exception when its
 * {@link com.github.fge.jsonschema.core.report.ProcessingMessage#asException()} is
 * called.</p>
 */
package com.github.fge.jsonschema.core.exceptions;
