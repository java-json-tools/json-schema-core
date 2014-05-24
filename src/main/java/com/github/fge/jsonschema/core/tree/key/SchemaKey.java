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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.google.common.base.Preconditions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import javax.annotation.Untainted;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Base class for a schema identifier, or "key"
 *
 * <p>This package offers two ways to load a schema: either directly (that is,
 * from a {@link JsonNode}) or via a URI. The implementation considers that
 * schemas loaded via the first mechanism are anonymous (that is, they have no
 * associated URI).</p>
 *
 * <p>Depending on which of the two ways above you use, a {@link SchemaTree}
 * will be identified either by an {@link AnonymousSchemaKey} (using {@link
 * #anonymousKey()}) or a {@link JsonRefSchemaKey} (using {@link
 * #forJsonRef(JsonRef)}).</p>
 */
@ParametersAreNonnullByDefault
public abstract class SchemaKey
{
    protected final JsonRef loadingRef;

    protected SchemaKey(final JsonRef loadingRef)
    {
        this.loadingRef = loadingRef;
    }

    /**
     * Generate an anonymous key for a {@link SchemaTree}
     *
     * <p>The key is uniquely identified by a {@code long} identifier; values
     * are picked from an {@link AtomicLong}.</p>
     *
     * <p>Note: this means that two identical JSON Schemas loaded anonymously at
     * different points in time (or different threads) will <strong>not</strong>
     * be considered equal.</p>
     *
     * @return a unique key for this schema
     */
    public static SchemaKey anonymousKey()
    {
        return new AnonymousSchemaKey();
    }

    /**
     * Generate a key for a schema loaded from a {@link JsonRef JSON Reference}
     *
     * @param ref the JSON Reference
     * @return the unique key for this schema
     *
     * @see URIDownloader
     */
    public static SchemaKey forJsonRef(@Untainted final JsonRef ref)
    {
        return new JsonRefSchemaKey(Preconditions.checkNotNull(ref));
    }

    /**
     * Get the identifier, as a long, for this schema -- DO NOT USE DIRECTLY
     *
     * <p><strong>Important</strong>: this method is here only for backwards
     * compatibility reasons (see {@link SchemaTree#getId()}); you should not
     * be using it as a reliable identifier, since for all non anonymous
     * schema keys (ie, instances of {@link JsonRefSchemaKey}), this will return
     * {@code 0L}.</p>
     *
     * @return see description
     */
    public abstract long getId();

    /**
     * Get the loading URI (as a {@link JsonRef JSON Reference}) for that key
     *
     * <p><strong>Important</strong>: this method only works reliably for non
     * anonymous schemas; for anonymous schemas, it will always return {@link
     * JsonRef#emptyRef()}. As such, you should not use it directly.</p>
     *
     * @return the JSON Reference used to load the schema (see description)
     */
    public final JsonRef getLoadingRef()
    {
        return loadingRef;
    }

    @Override
    public abstract int hashCode();

    @Override
    public abstract boolean equals(@Nullable final Object obj);

    @Nonnull
    @Override
    public abstract String toString();
}
