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

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.Frozen;
import com.github.fge.Thawed;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.SchemaLoader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.load.URIManager;
import com.github.fge.jsonschema.tree.CanonicalSchemaTree;
import com.github.fge.jsonschema.tree.InlineSchemaTree;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
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
     * Dictionary for URI downloaders
     *
     * @see URIDownloader
     * @see URIManager
     */
    final Dictionary<URIDownloader> downloaders;

    /**
     * Loading URI namespace
     *
     * @see SchemaLoader
     */
    final URI namespace;

    /**
     * Dereferencing mode
     *
     * @see SchemaLoader
     * @see CanonicalSchemaTree
     * @see InlineSchemaTree
     */
    final Dereferencing dereferencing;

    /**
     * Schema redirections
     */
    final Map<URI, URI> schemaRedirects;

    /**
     * Map of preloaded schemas
     */
    final Map<URI, JsonNode> preloadedSchemas;

    /**
     * Strip Javascript comments from downloaded schemas
     *
     * @see URIManager
     */
    final boolean stripJavascriptComments;

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
     * @param cfg the thawed configuration
     * @see LoadingConfigurationBuilder#freeze()
     */
    LoadingConfiguration(final LoadingConfigurationBuilder cfg)
    {
        downloaders = cfg.downloaders.freeze();
        namespace = cfg.namespace;
        dereferencing = cfg.dereferencing;
        schemaRedirects = ImmutableMap.copyOf(cfg.schemaRedirects);
        preloadedSchemas = ImmutableMap.copyOf(cfg.preloadedSchemas);
        stripJavascriptComments = cfg.stripJavascriptComments;
    }

    /**
     * Return the dictionary of URI downloaders
     *
     * @return an immutable {@link Dictionary}
     */
    public Dictionary<URIDownloader> getDownloaders()
    {
        return downloaders;
    }

    /**
     * Return the URI namespace
     *
     * @return the namespace
     */
    public URI getNamespace()
    {
        return namespace;
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
     * Return the map of schema redirects
     *
     * @return an immutable map of schema redirects
     */
    public Map<URI, URI> getSchemaRedirects()
    {
        return schemaRedirects;
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
     * Return the stripJavascriptComments mode used for this configuration
     *
     * @return the stripJavascriptComments mode
     */
    public boolean isStripJavascriptComments()
    {
        return stripJavascriptComments;
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
