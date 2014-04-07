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

package com.github.fge.jsonschema.core.load.uri;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;

import java.net.URI;

/**
 * Builder for a {@link URITranslatorConfiguration}
 *
 * <p>Example:</p>
 *
 * <pre>
 *     final URITranslatorConfiguration cfg
 *         = URITranslatorConfiguration.newBuilder()
 *         .setNamespace("http://my.site/myschemas/")
 *         .addPathRedirect("http://my.site/myschemas/", "resource:/com/foo/myschemas")
 *         .addSchemaRedirect("http://some.other.site/schema.json", "resource:/com/foo/externalsite/schema.json")
 *         .freeze();
 * </pre>
 *
 * <p>When feeding this configuration into a {@link LoadingConfiguration}
 * (using {@link LoadingConfigurationBuilder#setURITranslatorConfiguration(URITranslatorConfiguration)}),
 * the following will take place for URI {@code schema1.json#/definitions/def1}:
 * </p>
 *
 * <ul>
 *     <li>first, it will be resolved against the defined namespace, giving
 *     {@code http://my.site/myschemas/schema1.json#/definitions/def1};</li>
 *     <li>then a path redirection triggers: the URI used by the {@link
 *     SchemaLoader} will then be {@code
 *     resource:/com/foo/myschemas/schema1.json#/definitions/def1}.</li>
 * </ul>
 *
 * <p>If URI {@code http://some.other.site/schema.json#bar} is encountered,
 * a schema redirect triggers and will yield URI {@code
 resource:/com/foo/externalsite/schema.json#bar}.</p>
 *
 * @see URITranslatorConfiguration
 * @see SchemaLoader
 * @see LoadingConfigurationBuilder#setURITranslatorConfiguration(URITranslatorConfiguration)
 * @see SchemaLoader#SchemaLoader(LoadingConfiguration)
 */
public final class URITranslatorConfigurationBuilder
    implements Thawed<URITranslatorConfiguration>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final URI EMPTY = URI.create("");

    URI namespace = EMPTY;

    final PathRedirectRegistry pathRedirects = new PathRedirectRegistry();

    final SchemaRedirectRegistry schemaRedirects = new SchemaRedirectRegistry();

    URITranslatorConfigurationBuilder()
    {
    }

    URITranslatorConfigurationBuilder(final URITranslatorConfiguration cfg)
    {
        namespace = cfg.namespace;
        pathRedirects.putAll(cfg.pathRedirects);
        schemaRedirects.putAll(cfg.schemaRedirects);
    }

    /**
     * Set the namespace for this configuration
     *
     * <p>All schema loading via URIs (using {@link SchemaLoader#get(URI)} or
     * when encountering a JSON Reference in a schema) will be resolved against
     * the provided namespace.</p>
     *
     * @param uri the URI
     * @return this
     * @throws NullPointerException URI is null
     * @throws IllegalArgumentException URI is not absolute, or is not a path
     * URI (ie, does not end with {@code /})
     */
    public URITranslatorConfigurationBuilder setNamespace(final URI uri)
    {
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        final URI normalized = URIUtils.normalizeURI(uri);
        URIUtils.checkPathURI(normalized);
        namespace = normalized;
        return this;
    }

    /**
     * Set the namespace for this configuration (convenience method)
     *
     * <p>This calls {@link #setNamespace(URI)} after creating the URI using
     * {@link URI#create(String)}.</p>
     *
     * @param uri the URI
     * @return this
     * @throws NullPointerException argument is null
     * @throws IllegalArgumentException {@link URI#create(String)} failed
     *
     * @see #setNamespace(URI)
     */
    public URITranslatorConfigurationBuilder setNamespace(final String uri)
    {
        BUNDLE.checkNotNull(uri, "uriChecks.nullInput");
        return setNamespace(URI.create(uri));
    }

    /**
     * Add a schema redirection
     *
     * <p>Schema redirection occurs after namespace resolution and after path
     * redirection.</p>
     *
     * @param from the URI to redirect from
     * @param to the URI to redirect to
     * @return this
     * @throws NullPointerException one, or both, argument(s) is/are null
     * @throws IllegalArgumentException one, or both, URI(s) are not absolute
     * JSON References; or a redirection already exists for URI {@code from}; or
     * {@code from} and {@code to} are the same URI after normalization.
     */
    public URITranslatorConfigurationBuilder addSchemaRedirect(final URI from,
        final URI to)
    {
        schemaRedirects.put(from, to);
        return this;
    }

    /**
     * Convenience method for schema redirections
     *
     * <p>This calls {@link #addSchemaRedirect(URI, URI)} after converting its
     * string arguments to URIs using {@link URI#create(String)}.</p>
     *
     * @param from the URI to redirect from
     * @param to the URI to redirect to
     * @return this
     * @throws NullPointerException one, or both, argument(s) is/are null
     * @throws IllegalArgumentException {@link URI#create(String)} failed for
     * one or both argument(s)
     */
    public URITranslatorConfigurationBuilder addSchemaRedirect(
        final String from, final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        return addSchemaRedirect(URI.create(from), URI.create(to));
    }

    /**
     * Add a path redirection
     *
     * <p>What is called a "path URI" here is a URI which is absolute,
     * hierarchical, has no fragment part and whose path component ends with a
     * {@code /}.</p>
     *
     * @param from the "path URI" to redirect
     * @param to the target "path URI"
     * @return this
     * @throws NullPointerException one, or both, argument(s) is/are null
     * @throws IllegalArgumentException one, or both, argument(s) is/are not
     * valid path URIs; or a path redirection already exists for {@code from};
     * or {@code from} and {@code to} are the same URI after normalization.
     */
    public URITranslatorConfigurationBuilder addPathRedirect(final URI from,
        final URI to)
    {
        pathRedirects.put(from, to);
        return this;
    }

    /**
     * Convenience method for adding a path URI redirection
     *
     * <p>This calls {@link #addPathRedirect(URI, URI)} after performing {@link
     * URI#create(String)} on both arguments.</p>
     *
     * @param from the "path URI" to redirect
     * @param to the target "path URI"
     * @return this
     * @throws NullPointerException one, or both, argument(s) is/are null
     * @throws IllegalArgumentException {@link URI#create(String)} failed
     *
     * @see #addPathRedirect(URI, URI)
     */
    public URITranslatorConfigurationBuilder addPathRedirect(final String from,
        final String to)
    {
        BUNDLE.checkNotNull(from, "uriChecks.nullInput");
        BUNDLE.checkNotNull(to, "uriChecks.nullInput");
        return addPathRedirect(URI.create(from), URI.create(to));
    }

    /**
     * Obtain a frozen configuration from this builder
     *
     * @return a new {@link URITranslatorConfiguration}
     */
    @Override
    public URITranslatorConfiguration freeze()
    {
        return new URITranslatorConfiguration(this);
    }
}
