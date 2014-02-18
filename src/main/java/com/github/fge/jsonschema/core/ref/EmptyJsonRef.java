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

package com.github.fge.jsonschema.core.ref;

/**
 * A completely empty JSON Reference (ie, {@code #})
 *
 * <p>This happens in a lot of situations, it is therefore beneficial to have
 * a dedicated class for it. For instance, resolving any other reference against
 * this one always returns the other reference, and it is never absolute.</p>
 */
final class EmptyJsonRef
    extends JsonRef
{
    private static final JsonRef INSTANCE = new EmptyJsonRef();

    private EmptyJsonRef()
    {
        super(HASHONLY_URI);
    }

    static JsonRef getInstance()
    {
        return INSTANCE;
    }

    @Override
    public boolean isAbsolute()
    {
        return false;
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        return other;
    }
}
