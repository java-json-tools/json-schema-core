package com.github.fge.jsonschema.util;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import javax.inject.Provider;
import java.util.Map;

/**
 * A registry builder with key/value/pair normalization and checking
 *
 * <p>Note that null keys or values are not allowed.</p>
 *
 * @param <K> type of the keys
 * @param <V> type of the values
 *
 * @since 1.1.9
 */
@Beta
public abstract class Registry<K, V>
    implements Provider<Map<K, V>>
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final Map<K, V> map = Maps.newHashMap();
    private final Function<K, K> keyNormalizer;
    private final ArgumentChecker<K> keyChecker;
    private final Function<V, V> valueNormalizer;
    private final ArgumentChecker<V> valueChecker;

    /**
     * Protected constructor
     *
     * @param keyNormalizer the key normalizer
     * @param keyChecker the key checker
     * @param valueNormalizer the value normalizer
     * @param valueChecker the value checker
     * @throws NullPointerException one normalizer or checker is null
     */
    protected Registry(final Function<K, K> keyNormalizer,
        final ArgumentChecker<K> keyChecker,
        final Function<V, V> valueNormalizer,
        final ArgumentChecker<V> valueChecker)
    {
        this.keyNormalizer = BUNDLE.checkNotNull(keyNormalizer,
            "mapBuilder.nullNormalizer");
        this.keyChecker = BUNDLE.checkNotNull(keyChecker,
            "mapBuilder.nullChecker");
        this.valueNormalizer = BUNDLE.checkNotNull(valueNormalizer,
            "mapBuilder.nullNormalizer");
        this.valueChecker = BUNDLE.checkNotNull(valueChecker,
            "mapBuilder.nullChecker");
    }

    /**
     * Add a key/value pair in this registry
     *
     * <p>Both the keys and values are first normalized, then checked; finally,
     * before insertion, the key/value pair is checked.</p>
     *
     * @param key the key
     * @param value the value
     * @return this
     * @throws NullPointerException the key or value is null
     * @throws IllegalArgumentException see {@link ArgumentChecker}
     */
    public final Registry<K, V> put(final K key, final V value)
    {
        BUNDLE.checkNotNull(key, "mapBuilder.nullKey");
        BUNDLE.checkNotNull(value, "mapBuilder.nullValue");

        final K normalizedKey = keyNormalizer.apply(key);
        keyChecker.check(key);

        final V normalizedValue = valueNormalizer.apply(value);
        valueChecker.check(value);

        checkEntry(normalizedKey, normalizedValue);

        map.put(normalizedKey, normalizedValue);
        return this;
    }

    /**
     * Put the contents from another map into this map builder
     *
     * <p>This calls {@link #put(Object, Object)} on each key/value pair in the
     * map.</p>
     *
     * @param otherMap the map
     * @return this
     * @throws NullPointerException map is null
     */
    public final Registry<K, V> putAll(final Map<K, V> otherMap)
    {
        BUNDLE.checkNotNull(otherMap, "mapBuilder.nullMap");

        for (final Map.Entry<K, V> entry: otherMap.entrySet())
            put(entry.getKey(), entry.getValue());

        return this;
    }

    /**
     * Remove one key and its associated value from the registry
     *
     * @param key the key to remove
     * @return this
     */
    public final Registry<K, V> remove(final K key)
    {
        map.remove(key);
        return this;
    }

    /**
     * Clear all entries from this registry
     *
     * @return this
     */
    public final Registry<K, V> clear()
    {
        map.clear();
        return this;
    }

    /**
     * Build the map
     *
     * <p>The returned map is immutable.</p>
     *
     * @return a map
     * @see ImmutableMap
     */
    public final Map<K, V> build()
    {
        return ImmutableMap.copyOf(map);
    }

    @Override
    public final Map<K, V> get()
    {
        return build();
    }

    /**
     * Check the validity of the entry before submitting it
     *
     * <p>Note that the key and value are normalized when entering this
     * method, and that they cannot be {@code null}.</p>
     *
     * @param key the normalized key
     * @param value the normalized value
     */
    protected abstract void checkEntry(final K key, final V value);
}
