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

package com.github.fge.jsonschema.core.util;

import com.github.fge.Frozen;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.Immutable;
import java.util.Map;

/**
 * A dictionary
 *
 * <p>This class is a wrapper over a {@link Map}, where keys are always {@link
 * String}s. The type of values is arbitrary.</p>
 *
 * <p>This class is <b>immutable</b>. If you want to build a dictionary, you
 * have two options:</p>
 *
 * <ul>
 *     <li>create a new builder using {@link #newBuilder()};</li>
 *     <li>create a builder with all elements from an existing dictionary using
 *     {@link #thaw()}.</li>
 * </ul>
 *
 * <p>For instance:</p>
 *
 * <pre>
 *     // New builder
 *     final DictionaryBuilder&lt;Foo&gt; builder = Dictionary.newBuilder();
 *     // From an existing dictionary
 *     final DictionaryBuilder&lt;Foo&gt; builder = dict.thaw();
 * </pre>
 *
 * @param <T> the type of values for this dictionary
 *
 * @see DictionaryBuilder
 */
@Immutable
public final class Dictionary<T>
    implements Frozen<DictionaryBuilder<T>>
{
    /**
     * Entries of this dictionary
     *
     * <p>This map is <b>immutable</b>.</p>
     *
     * @see ImmutableMap
     */
    final Map<String, T> entries;

    /**
     * Return a new, empty builder for a dictionary of this type
     *
     * @param <T> the type of values
     * @return a new, empty builder
     */
    public static <T> DictionaryBuilder<T> newBuilder()
    {
        return new DictionaryBuilder<T>();
    }

    /**
     * Package local constructor to generate a dictionary from a builder
     *
     * @param builder the builder
     * @see DictionaryBuilder#freeze()
     */
    Dictionary(final DictionaryBuilder<T> builder)
    {
        entries = ImmutableMap.copyOf(builder.entries);
    }

    /**
     * Return the entries from this dictionary as a map
     *
     * <p>The returned map is <b>immutable</b>.</p>
     *
     * @return an immutable map of entries
     * @see ImmutableMap
     */
    public Map<String, T> entries()
    {
        return entries;
    }

    /**
     * Return a builder with a copy of all entries from this dictionary
     *
     * @return a {@link DictionaryBuilder}
     */
    @Override
    public DictionaryBuilder<T> thaw()
    {
        return new DictionaryBuilder<T>(this);
    }
}
