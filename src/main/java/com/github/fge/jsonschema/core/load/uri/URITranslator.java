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

import com.github.fge.jsonschema.core.load.SchemaLoader;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.util.URIUtils;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

/**
 * URI translation
 *
 * <p>When it is required that a URI be dereferenced (either by yourself,
 * using {@link SchemaLoader#get(URI)}, or when a JSON Reference is encountered
 * in a JSON Schema), this class is in charge of translating the
 * <em>resolved</em> URI into a more suitable URI for your environment.</p>
 *
 * <p>Translation is done in three steps:</p>
 *
 * <ul>
 *     <li>resolving against the default namespace,</li>
 *     <li>translating the path to another one (if applicable),</li>
 *     <li>translating the full schema URI to another one (if applicable).</li>
 * </ul>
 *
 * <p>By default, the namespace is empty and no path or schema translations are
 * defined.</p>
 *
 * @see URITranslatorConfiguration
 */
public final class URITranslator
{
    private final URI namespace;
    private final Map<URI, URI> pathRedirects;
    private final Map<URI, URI> schemaRedirects;

    public URITranslator(final URITranslatorConfiguration cfg)
    {
        namespace = cfg.namespace;
        pathRedirects = ImmutableMap.copyOf(cfg.pathRedirects);
        schemaRedirects = ImmutableMap.copyOf(cfg.schemaRedirects);
    }

    public URI translate(final URI source)
    {
        URI uri = URIUtils.normalizeURI(namespace.resolve(source));
        final String fragment = uri.getFragment();

        try {
            uri = new URI(uri.getScheme(), uri.getSchemeSpecificPart(), null);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("How did I get there??", e);
        }

        for (final Map.Entry<URI, URI> entry: pathRedirects.entrySet()) {
            final URI relative = entry.getKey().relativize(uri);
            if (!relative.equals(uri))
                uri = entry.getValue().resolve(relative);
        }

        uri = JsonRef.fromURI(uri).getLocator();

        if (schemaRedirects.containsKey(uri))
            uri = schemaRedirects.get(uri);

        try {
            return new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                fragment);
        } catch (URISyntaxException e) {
            throw new IllegalStateException("How did I get there??", e);
        }
    }
}
