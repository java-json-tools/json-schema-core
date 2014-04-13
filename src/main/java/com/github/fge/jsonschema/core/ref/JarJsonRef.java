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

package com.github.fge.jsonschema.core.ref;

import java.net.URI;

/**
 * Special case of a JSON Reference with a JAR URL
 *
 * <p>These URLs are legal URIs; trouble is, while they are absolute, they are
 * also opaque (meaning their path component does not start with a {@code /},
 * see {@link URI}).</p>
 *
 * <p>This class therefore adds a special case for URI resolution by extracting
 * the "real" path component out of the JAR URL and applying path resolution
 * against that extracted path. While this works, this is a violation of the
 * URI RFC.</p>
 *
 * @see HierarchicalJsonRef
 */
final class JarJsonRef
    extends JsonRef
{
    /**
     * The URL part with the {@code !} included
     */
    private final String jarPrefix;

    /**
     * Everything after the {@code !}
     */
    private final URI pathURI;

    /**
     * Build a JSON Reference form a JAR URL
     *
     * @param uri the URI
     */
    JarJsonRef(final URI uri)
    {
        super(uri);
        final String str = uri.toString();
        final int index = str.indexOf('!');
        jarPrefix = str.substring(0, index + 1);

        final String path = str.substring(index + 1);
        pathURI = URI.create(path);
    }

    /**
     * Specialized constructor used when resolving against a relative URI
     *
     * @param uri the final URI
     * @param jarPrefix the jar prefix
     * @param pathURI the path
     */
    private JarJsonRef(final URI uri, final String jarPrefix, final URI pathURI)
    {
        super(uri);
        this.jarPrefix = jarPrefix;
        this.pathURI = pathURI;
    }

    @Override
    public boolean isAbsolute()
    {
        return legal && pointer.isEmpty();
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        if (other.uri.isAbsolute())
            return other;

        final URI targetPath = pathURI.resolve(other.uri);
        final URI targetURI = URI.create(jarPrefix + targetPath.toString());
        return new JarJsonRef(targetURI, jarPrefix, targetPath);
    }
}
