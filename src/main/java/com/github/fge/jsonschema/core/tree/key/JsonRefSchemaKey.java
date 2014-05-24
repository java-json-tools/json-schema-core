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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

final class JsonRefSchemaKey
    extends SchemaKey
{
    JsonRefSchemaKey(final JsonRef ref)
    {
        super(ref);
    }

    @Override
    public long getId()
    {
        return 0L;
    }

    @Override
    public int hashCode()
    {
        return loadingRef.hashCode();
    }

    @Override
    public boolean equals(@Nullable final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final JsonRefSchemaKey other = (JsonRefSchemaKey) obj;
        return loadingRef.equals(other.loadingRef);
    }

    @Nonnull
    @Override
    public String toString()
    {
        return "loaded from JSON ref " + loadingRef;
    }
}
