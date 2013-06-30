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

/**
 * Exceptions and exception provider
 *
 * <p>This package contains checked exceptions raised by the API core. The base
 * exception is {@link com.github.fge.jsonschema.exceptions.ProcessingException}
 * and all other exceptions, save for unchecked exceptions, inherit it.</p>
 *
 * <p>The {@link com.github.fge.jsonschema.exceptions.ExceptionProvider}
 * interface can be used by your own custom processors to set custom
 * exceptions in messages: {@link
 * com.github.fge.jsonschema.report.ProcessingMessage} accepts such a provider
 * and will then return the appropriate exception when its
 * {@link com.github.fge.jsonschema.report.ProcessingMessage#asException()} is
 * called.</p>
 */
package com.github.fge.jsonschema.exceptions;
