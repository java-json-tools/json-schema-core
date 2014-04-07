/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.load.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.Frozen;
import com.github.fge.Thawed;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.load.Dereferencing;
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.load.URIManager;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.core.tree.InlineSchemaTree;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.EnumSet;
import java.util.Map;

/**
 * Loading configuration (frozen instance)
 *
 * <p>With a loading configuration, you can influence the following aspects:</p>
 *
 * <ul>
 *     <li>what schemas should be preloaded;</li>
 *     <li>what URI schemes should be supported;</li>
 *     <li>whether we want to cache loaded schemas;</li>
 *     <li>how to resolve URIs (see {@link URITranslatorConfiguration});</li>
 *     <li>what dereferencing mode should be used.</li>
 * </ul>
 *
 * <p>The default configuration only preloads the core metaschemas for draft v4
 * and draft v3, and uses canonical dereferencing mode; it also uses the default
 * set of supported schemes:</p>
 *
 * <ul>
 *     <li>{@code file},</li>
 *     <li>{@code http},</li>
 *     <li>{@code https},</li>
 *     <li>{@code ftp},</li>
 *     <li>{@code resource} (resource in the classpath),</li>
 *     <li>{@code jar} (jar URL).</li>
 * </ul>
 *
 * <p>You don't instantiate this class directly, you must go through a {@link
 * LoadingConfigurationBuilder} for this (using {@link #newBuilder()};
 * alternatively, you can obtain a default configuration using {@link
 * #byDefault()}.</p>
 *
 * @see LoadingConfigurationBuilder
 * @see Dereferencing
 * @see URIManager
 * @see SchemaLoader
 */
public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    /**
     * Map of URI downloaders
     *
     * @see URIDownloader
     * @see URIManager
     */
    final Map<String, URIDownloader> downloaders;

    final URITranslatorConfiguration translatorCfg;
    
    /**
     * If we have to cache loaded schemas, note that this do not affect preloadedSchema that are always cached
     */
    final boolean enableCache;

    /**
     * Dereferencing mode
     *
     * @see SchemaLoader
     * @see CanonicalSchemaTree
     * @see InlineSchemaTree
     */
    final Dereferencing dereferencing;

    /**
     * Map of preloaded schemas
     */
    final Map<URI, JsonNode> preloadedSchemas;

    /**
     * Set of JsonParser features to be enabled while loading schemas
     *
     * <p>The set of JavaParser features used to construct ObjectMapper/
     * ObjectReader instances used to load schemas</p>
     */
    final EnumSet<JsonParser.Feature> parserFeatures;

    /**
     * ObjectReader configured with enabled JsonParser features
     *
     * <p>Object reader configured using enabled JsonParser features and
     * minimum requirements enforced by JacksonUtils.</p>
     *
     * @see JacksonUtils#newMapper()
     */
    private final ObjectMapper objectMapper;

    /**
     * Create a new, default, mutable configuration instance
     *
     * @return a {@link LoadingConfigurationBuilder}
     */
    public static LoadingConfigurationBuilder newBuilder()
    {
        return new LoadingConfigurationBuilder();
    }

    /**
     * Create a default, immutable loading configuration
     *
     * <p>This is the result of calling {@link Thawed#freeze()} on {@link
     * #newBuilder()}.</p>
     *
     * @return a default configuration
     */
    public static LoadingConfiguration byDefault()
    {
        return new LoadingConfigurationBuilder().freeze();
    }

    /**
     * Create a frozen loading configuration from a thawed one
     *
     * @param builder the thawed configuration
     * @see LoadingConfigurationBuilder#freeze()
     */
    LoadingConfiguration(final LoadingConfigurationBuilder builder)
    {
        downloaders = builder.downloaders.build();
        translatorCfg = builder.translatorCfg;
        dereferencing = builder.dereferencing;
        preloadedSchemas = ImmutableMap.copyOf(builder.preloadedSchemas);
        parserFeatures = EnumSet.copyOf(builder.parserFeatures);
        objectMapper = constructObjectMapper();
        enableCache = builder.enableCache;
    }

    /**
     * Construct a JacksonUtils compatible ObjectMapper with a set of JsonParser
     * features.
     *
     * @return configured ObjectMapper
     * @see JacksonUtils#newMapper()
     */
    private ObjectMapper constructObjectMapper()
    {
        final ObjectMapper mapper = JacksonUtils.newMapper();

        // enable JsonParser feature configurations
        for (final JsonParser.Feature feature : parserFeatures)
            mapper.configure(feature, true);
        return mapper;
    }

    /**
     * Return the map of downloaders for this configuration
     *
     * @return an {@link ImmutableMap} of downloaders
     *
     * @since 1.1.9
     */
    public Map<String, URIDownloader> getDownloaderMap()
    {
        return downloaders; // ImmutableMap
    }

    public URITranslatorConfiguration getTranslatorConfiguration()
    {
        return translatorCfg;
    }

    /**
     * Return the dereferencing mode used for this configuration
     *
     * @return the dereferencing mode
     */
    public Dereferencing getDereferencing()
    {
        return dereferencing;
    }

    /**
     * Return the map of preloaded schemas
     *
     * @return an immutable map of preloaded schemas
     */
    public Map<URI, JsonNode> getPreloadedSchemas()
    {
        return preloadedSchemas;
    }

    /**
     * Get a configured {@link ObjectMapper}
     *
     * @return the ObjectMapper
     * @see JacksonUtils#newMapper()
     */
    public ObjectMapper getObjectMapper()
    {
        return objectMapper;
    }
    
    /**
     * Return if we want to cache loaded schema or not
     * note that this do not affect preloadedSchema that are always cached
     * 
     * @return if the cache has to be enabled
     */
    public boolean getEnableCache() {
        return enableCache;
    }

    /**
     * Return a thawed version of this loading configuration
     *
     * @return a thawed copy
     * @see LoadingConfigurationBuilder#LoadingConfigurationBuilder(LoadingConfiguration)
     */
    @Override
    public LoadingConfigurationBuilder thaw()
    {
        return new LoadingConfigurationBuilder(this);
    }
}
