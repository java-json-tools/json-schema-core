package com.github.fge.jsonschema.cfg;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.load.Dereferencing;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.util.Frozen;
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
 *     <li>if we want to cache loaded schemas.</li>
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
 * @see com.github.fge.jsonschema.load.Dereferencing
 * @see com.github.fge.jsonschema.load.URIManager
 * @see com.github.fge.jsonschema.load.SchemaLoader
 */
public final class LoadingConfiguration
    implements Frozen<LoadingConfigurationBuilder>
{
    /**
     * Dictionary for URI downloaders
     *
     * @see com.github.fge.jsonschema.load.URIDownloader
     * @see com.github.fge.jsonschema.load.URIManager
     */
    final Dictionary<URIDownloader> downloaders;

    /**
     * Loading URI namespace
     *
     * @see com.github.fge.jsonschema.load.SchemaLoader
     */
    final URI namespace;

    /**
     * If we have to cache loaded schemas, note that this do not affect
     * preloaded schemas which are always cached.
     */
    final boolean enableCache;

    /**
     * Dereferencing mode
     *
     * @see com.github.fge.jsonschema.load.SchemaLoader
     * @see com.github.fge.jsonschema.tree.CanonicalSchemaTree
     * @see com.github.fge.jsonschema.tree.InlineSchemaTree
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
     * <p>This is the result of calling {@link com.github.fge.jsonschema.util.Thawed#freeze()} on {@link
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
        enableCache = cfg.enableCache;
    }

    /**
     * Return the dictionary of URI downloaders
     *
     * @return an immutable {@link com.github.fge.jsonschema.library.Dictionary}
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
     * Do we want to cache schemas?
     *
     * <p>Note that this setting will <b>note</b> affect preloaded
     * schemas.</p>
     *
     * @return {@code true} if schema caching is enabled
     * @since 1.0.3
     */
    public boolean getEnableCache()
    {
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
