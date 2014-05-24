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

package com.github.fge.jsonschema.core.tree.key;

import com.github.fge.jsonschema.core.ref.JsonRef;
import com.google.common.primitives.Longs;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicLong;

public final class AnonymousSchemaKey
    extends SchemaKey
{
    private static final AtomicLong ID_GEN = new AtomicLong(0L);

    private final long id;

    AnonymousSchemaKey(final JsonRef ref)
    {
        super(ref);
        id = ID_GEN.getAndIncrement();
    }

    @Override
    public int hashCode()
    {
        return Longs.hashCode(id);
    }

    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AnonymousSchemaKey other = (AnonymousSchemaKey) obj;
        return id == other.id;
    }

    @Nonnull
    @Override
    public String toString()
    {
        return "anonymous; id = " + id;
    }
}
