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

package com.github.fge.jsonschema.core.report;

import com.github.fge.jsonschema.core.exceptions.ProcessingException;

/**
 * Message log levels
 *
 * <p>A special log level, {@link #NONE}, can be used by processors wishing to
 * implement "unchecked" validation (ie, capture {@link ProcessingException}s
 * and report them instead of throwing them).</p>
 *
 * <p>All messages within {@link ProcessingException}s have level {@link
 * #FATAL}.</p>
 */
public enum LogLevel
{
    DEBUG("debug"),
    INFO("info"),
    WARNING("warning"),
    ERROR("error"),
    FATAL("fatal"),
    NONE("none"),
    ;

    private final String s;

    LogLevel(final String s)
    {
        this.s = s;
    }

    @Override
    public String toString()
    {
        return s;
    }
}

