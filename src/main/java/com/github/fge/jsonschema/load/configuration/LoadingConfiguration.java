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

package com.github.fge.jsonschema.load.configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.github.fge.Frozen;
import com.github.fge.Thawed;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.load.URIManager;
import com.github.fge.jsonschema.load.transform.URITransformer;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.InlineSchemaTree;
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
 *     <li>set a default namespace for loading schemas from URIs;</li>
 *     <li>add redirections from one schema URI to another;</li>
 *     <li>what dereferencing mode should be used.</li>
 * </ul>
 *
 * <p>The default configuration only preloads the core metaschemas for draft v4
 * and draft v3, and uses canonical dereferencing mode; it also uses the default
 * set of supported schemes.</p>
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

    final URITransformer transformer;

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
     * @see JacksonUtils#getReader()
     */
    final ObjectReader objectReader;

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
        transformer = builder.transformer;
        dereferencing = builder.dereferencing;
        preloadedSchemas = ImmutableMap.copyOf(builder.preloadedSchemas);
        parserFeatures = EnumSet.copyOf(builder.parserFeatures);
        objectReader = constructObjectReader();
    }

    /**
     * Construct JacksonUtils compatible ObjectReader using frozen JsonParser
     * features.
     *
     * TODO: move implementation to JacksonUtils
     *
     * @return configured ObjectReader
     * @see JacksonUtils#getReader()
     */
    private ObjectReader constructObjectReader()
    {
        // JacksonUtils compatible ObjectMapper configuration
        final ObjectMapper mapper = JacksonUtils.newMapper();

        // enable JsonParser feature configurations
        for (final JsonParser.Feature feature : parserFeatures)
            mapper.configure(feature, true);
        return mapper.reader();
    }

    /**
     * Return the dictionary of URI downloaders
     *
     * @return an immutable {@link Dictionary}
     *
     * @deprecated use {@link #getDownloaderMap()} instead. Will disappear in
     * 1.1.10.
     */
    @Deprecated
    public Dictionary<URIDownloader> getDownloaders()
    {
        final DictionaryBuilder<URIDownloader> builder
            = Dictionary.newBuilder();

        for (final Map.Entry<String, URIDownloader> entry:
            downloaders.entrySet())
            builder.addEntry(entry.getKey(), entry.getValue());

        return builder.freeze();
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

    public URITransformer getTransformer()
    {
        return transformer;
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
     * Return configured ObjectReader
     *
     * @return the ObjectReader
     */
    public ObjectReader getObjectReader()
    {
        return objectReader;
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
