package com.github.fge.jsonschema.util;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

@Beta
public abstract class MapBuilder<K, V>
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final Map<K, V> map = Maps.newHashMap();
    private final Function<K, K> keyNormalizer;
    private final ArgumentChecker<K> keyChecker;
    private final Function<V, V> valueNormalizer;
    private final ArgumentChecker<V> valueChecker;

    protected MapBuilder(final Function<K, K> keyNormalizer,
        final ArgumentChecker<K> keyChecker,
        final Function<V, V> valueNormalizer,
        final ArgumentChecker<V> valueChecker)
    {
        this.keyNormalizer = keyNormalizer;
        this.keyChecker = keyChecker;
        this.valueNormalizer = valueNormalizer;
        this.valueChecker = valueChecker;
    }

    public final MapBuilder<K, V> put(final K key, final V value)
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

    public final MapBuilder<K, V> putAll(final Map<K, V> otherMap)
    {
        BUNDLE.checkNotNull(otherMap, "mapBuilder.nullMap");

        for (final Map.Entry<K, V> entry: otherMap.entrySet())
            put(entry.getKey(), entry.getValue());

        return this;
    }

    public final Map<K, V> build()
    {
        return ImmutableMap.copyOf(map);
    }

    protected abstract void checkEntry(final K key, final V value);
}
