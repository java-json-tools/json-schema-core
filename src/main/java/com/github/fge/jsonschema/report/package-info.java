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
 * Processing report infrastructure
 *
 * <p>The core components of reporting are these three classes:</p>
 *
 * <ul>
 *     <li>{@link com.github.fge.jsonschema.report.ProcessingMessage} (an
 *     individual message);</li>
 *     <li>{@link com.github.fge.jsonschema.report.LogLevel} (the log level of a
 *     message);</li>
 *     <li>{@link com.github.fge.jsonschema.report.ProcessingReport} (interface
 *     to a processing report).</li>
 * </ul>
 *
 * <p>The other important interface in this package is {@link
 * com.github.fge.jsonschema.report.MessageProvider}: all inputs and outputs
 * of processors are required to implement it; its goal is for processors to
 * be able to grab a message template reflecting the current processing context.
 * </p>
 */
package com.github.fge.jsonschema.report;
