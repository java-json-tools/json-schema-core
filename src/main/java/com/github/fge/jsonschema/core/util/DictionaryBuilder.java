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

package com.github.fge.jsonschema.core.util;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Maps;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Map;

/**
 * A dictionary builder
 *
 * <p>This is the "thawed", alterable form of a {@link Dictionary}.</p>
 *
 * <p>To build a dictionary out of this class, use {@link #freeze()}.</p>
 *
 * <p>All mutation methods return {@code this}, so you can chain
 * additions/deletions:</p>
 *
 * <pre>
 *     final Dictionary&lt;Foo&gt; dict = Dictionary.newBuilder()
 *         .addEntry("foo1", foo1).addEntry("foo2", foo2).freeze();
 * </pre>
 *
 * @param <T> the type of elements for this dictionary builder
 */
@NotThreadSafe
public final class DictionaryBuilder<T>
    implements Thawed<Dictionary<T>>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * Entries for this builder (mutable!)
     */
    final Map<String, T> entries = Maps.newHashMap();

    /**
     * Package local constructor returning an empty builder
     *
     * @see Dictionary#newBuilder()
     */
    DictionaryBuilder()
    {
    }

    /**
     * Instantiate a builder with all entries from an existing dictionary
     *
     * @param dict the source dictionary
     * @see Dictionary#thaw()
     */
    DictionaryBuilder(final Dictionary<T> dict)
    {
        entries.putAll(dict.entries);
    }

    /**
     * Add one entry to this builder
     *
     * @param key the key
     * @param value the value
     * @return this
     * @throws NullPointerException either the key or the value is null
     */
    public DictionaryBuilder<T> addEntry(final String key, final T value)
    {
        BUNDLE.checkNotNull(key, "dictionary.nullKey");
        BUNDLE.checkNotNull(value, "dictionary.nullValue");
        entries.put(key, value);
        return this;
    }

    /**
     * Add all entries from another dictionary
     *
     * @param other the other dictionary
     * @return this
     * @throws NullPointerException the dictionary is null
     */
    public DictionaryBuilder<T> addAll(final Dictionary<T> other)
    {
        BUNDLE.checkNotNull(other, "dictionary.nullDict");
        entries.putAll(other.entries);
        return this;
    }

    /**
     * Remove one entry from this builder
     *
     * @param key the key to remove
     * @return this
     */
    public DictionaryBuilder<T> removeEntry(final String key)
    {
        entries.remove(key);
        return this;
    }

    /**
     * Build an immutable dictionary out of this builder
     *
     * @return a new {@link Dictionary} with all elements from this builder
     */
    @Override
    public Dictionary<T> freeze()
    {
        return new Dictionary<T>(this);
    }
}
