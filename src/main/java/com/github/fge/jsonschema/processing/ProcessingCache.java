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

package com.github.fge.jsonschema.processing;

import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessingError;
import com.github.fge.jsonschema.exceptions.unchecked.ProcessorBuildError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.base.Equivalence;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.jcip.annotations.ThreadSafe;

import java.util.concurrent.ExecutionException;

import static com.github.fge.jsonschema.messages.ProcessingErrors.*;
import static com.google.common.base.Equivalence.Wrapper;

/**
 * A processing result cache usable in {@link Processor}s
 *
 * <p>This is a wrapper over Guava's {@link LoadingCache} to which you pass
 * two arguments:</p>
 *
 * <ul>
 *     <li>an {@link Equivalence} on cache keys,</li>
 *     <li>a {@link CacheLoader} to load keys.</li>
 * </ul>
 *
 * <p>Note that the cache loader <b>must</b> throw a {@link ProcessingException}
 * or any subclass.</p>
 *
 * <p>You can of course use Guava's builtin equivalences if you don't wish (or
 * need) to write your own:</p>
 *
 * <ul>
 *     <li>{@link Equivalence#equals()} for the classical equals/hashcode key
 *     comparison,</li>
 *     <li>{@link Equivalence#identity()} for a reference equality comparison
 *     (useful if you use enums or classes as keys, for instance).</li>
 * </ul>
 *
 * <p>Note that there is one <b>major</b> difference with Guava's caches: this
 * cache <b>does not accept null keys</b>.</p>
 *
 * @param <K> the type of (wrapped) keys for this cache
 * @param <V> the type of values retrieved by this cache
 *
 * @deprecated use {@link CachingProcessor} instead
 *
 * @see LoadingCache
 * @see Equivalence
 * @see Wrapper
 */
@ThreadSafe
@Deprecated
public final class ProcessingCache<K, V>
{
    /**
     * The internal loading cache
     */
    private final LoadingCache<Equivalence.Wrapper<K>, V> cache;

    /**
     * The equivalence for keys
     */
    private final Equivalence<K> equivalence;

    /**
     * Constructor
     *
     * @param equivalence the equivalence
     * @param cacheLoader the cache loader function
     * @throws ProcessorBuildError either the equivalence or the cache loader
     * is null
     */
    public ProcessingCache(final Equivalence<K> equivalence,
        final CacheLoader<Wrapper<K>, V> cacheLoader)
    {
        if (equivalence == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_EQUIVALENCE));
        if (cacheLoader == null)
            throw new ProcessorBuildError(new ProcessingMessage()
                .message(NULL_LOADER));
        this.equivalence = equivalence;
        cache = CacheBuilder.newBuilder().recordStats().build(cacheLoader);
    }

    /**
     * Get an entry from the cache
     *
     * @param key the key
     * @return the value
     * @throws ProcessingException error when computing the value
     * @throws ProcessingError key is null
     */
    public V get(final K key)
        throws ProcessingException
    {
        if (key == null)
            throw new ProcessingError(new ProcessingMessage()
                .message(NULL_KEYS_FORBIDDEN));
        try {
            return cache.get(equivalence.wrap(key));
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
    }

    /**
     * Get an entry from the cache, "ignoring" exceptions
     *
     * <p>Exceptions are not really ignored per se. You can use this method
     * instead of {@link #get(Object)} if you are sure that no exceptions can
     * be thrown.</p>
     *
     * <p>If an exception <i>is</i> thrown nevertheless, it will be an unchecked
     * exception.</p>
     *
     * @param key the key
     * @return the matching value
     * @throws ProcessingError key is null
     */
    public V getUnchecked(final K key)
    {
        if (key == null)
            throw new ProcessingError(new ProcessingMessage()
                .message(NULL_KEYS_FORBIDDEN));
        return cache.getUnchecked(equivalence.wrap(key));
    }

    @Override
    public String toString()
    {
        return cache.stats().toString();
    }
}
