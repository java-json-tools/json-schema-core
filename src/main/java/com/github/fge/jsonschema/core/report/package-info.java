/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Processing report infrastructure
 *
 * <p>The core components of reporting are these three classes:</p>
 *
 * <ul>
 *     <li>{@link com.github.fge.jsonschema.core.report.ProcessingMessage} (an
 *     individual message);</li>
 *     <li>{@link com.github.fge.jsonschema.core.report.LogLevel} (the log level of a
 *     message);</li>
 *     <li>{@link com.github.fge.jsonschema.core.report.ProcessingReport} (interface
 *     to a processing report).</li>
 * </ul>
 *
 * <p>The other important interface in this package is {@link
 * com.github.fge.jsonschema.core.report.MessageProvider}: all inputs and outputs
 * of processors are required to implement it; its goal is for processors to
 * be able to grab a message template reflecting the current processing context.
 * </p>
 */
package com.github.fge.jsonschema.core.report;
