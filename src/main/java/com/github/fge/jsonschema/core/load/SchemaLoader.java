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

package com.github.fge.jsonschema.core.load;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.load.uri.URITranslator;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheBuilderSpec;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;

import javax.annotation.concurrent.ThreadSafe;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * JSON Schema loader
 *
 * <p>All schema registering and downloading is done through this class.</p>
 *
 * <p>Note that if the id of a schema is not absolute (that is, the URI itself
 * is absolute and it has no fragment part, or an empty fragment), then the
 * whole schema will be considered anonymous.</p>
 *
 */
@ThreadSafe
public final class SchemaLoader
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * The URI manager
     */
    private final URIManager manager;

    /**
     * The URI translator
     */
    private final URITranslator translator;

    /**
     * Schema cache
     */
    private final LoadingCache<URI, JsonNode> cache;

    /**
     * Our dereferencing mode
     */
    private final Dereferencing dereferencing;

    /**
     * Map of preloaded schemas
     */
    private final Map<URI, JsonNode> preloadedSchemas;

    /**
     * Create a new schema loader with a given loading configuration
     *
     * @param cfg the configuration
     * @see LoadingConfiguration
     * @see LoadingConfigurationBuilder
     */
    public SchemaLoader(final LoadingConfiguration cfg)
    {
        translator = new URITranslator(cfg.getTranslatorConfiguration());
        dereferencing = cfg.getDereferencing();
        manager = new URIManager(cfg);
        preloadedSchemas = ImmutableMap.copyOf(cfg.getPreloadedSchemas());

        final CacheBuilder<Object, Object> cacheBuilder = cfg.getEnableCache()
            ? CacheBuilder.newBuilder()
            : CacheBuilder.from(CacheBuilderSpec.disableCaching());
        
        cache = cacheBuilder.build(new CacheLoader<URI, JsonNode>()
        {
            @Override
            public JsonNode load(final URI key)
                throws ProcessingException
            {
                return manager.getContent(key);
            }
        });
    }

    /**
     * Create a new schema loader with the default loading configuration
     */
    public SchemaLoader()
    {
        this(LoadingConfiguration.byDefault());
    }

    /**
     * Create a new tree from a schema
     *
     * <p>Note that it will always create an "anonymous" tree, that is a tree
     * with an empty loading URI.</p>
     *
     * @param schema the schema
     * @return a new tree
     * @see Dereferencing#newTree(JsonNode)
     * @throws NullPointerException schema is null
     */
    public SchemaTree load(final JsonNode schema)
    {
        BUNDLE.checkNotNull(schema, "loadingCfg.nullSchema");
        return dereferencing.newTree(schema);
    }

    /**
     * Get a schema tree from the given URI
     *
     * <p>Note that if the URI is relative, it will be resolved against this
     * registry's namespace, if any.</p>
     *
     * @param uri the URI
     * @return a schema tree
     * @throws ProcessingException URI is not an absolute JSON reference, or
     * failed to dereference this URI
     * @throws NullPointerException URI is null
     */
    public SchemaTree get(final URI uri)
        throws ProcessingException
    {
        final JsonRef ref = JsonRef.fromURI(translator.translate(uri));

        if (!ref.isAbsolute())
            throw new ProcessingException(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("refProcessing.uriNotAbsolute"))
                .putArgument("uri", ref));

        final URI realURI = ref.toURI();

        try {
            JsonNode node = preloadedSchemas.get(realURI);
            if (node == null)
                node = cache.get(realURI);
            return dereferencing.newTree(ref, node);
        } catch (ExecutionException e) {
            throw (ProcessingException) e.getCause();
        }
    }

    @Override
    public String toString()
    {
        return cache.toString();
    }
}
