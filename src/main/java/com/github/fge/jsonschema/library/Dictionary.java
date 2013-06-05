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

package com.github.fge.jsonschema.library;

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
