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

package com.github.fge.jsonschema.core.load.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * A downloader for the custom {@code resource} "scheme"
 *
 * <p>Here, {@code resource} is to be interpreted as a Java resource, exactly
 * what you would obtain using {@link Class#getResourceAsStream(String)}.</p>
 *
 * <p>And in fact, this is what this downloader does: it takes whatever is in
 * the provided URI's path (using {@link URI#getPath()}) and tries to make an
 * input stream of it. The difference is that an {@link IOException} will be
 * thrown if the resource cannot be found (instead of returning {@code null}).
 * </p>
 */
public final class ResourceURIDownloader
    implements URIDownloader
{
    private static final Class<ResourceURIDownloader> MYSELF
        = ResourceURIDownloader.class;

    private static final URIDownloader INSTANCE = new ResourceURIDownloader();

    private ResourceURIDownloader()
    {
    }

    public static URIDownloader getInstance()
    {
        return INSTANCE;
    }

    @Override
    public InputStream fetch(final URI source)
        throws IOException
    {
        final String resource = source.getPath();
        final InputStream in = MYSELF.getResourceAsStream(resource);

        if (in == null)
            throw new IOException("resource " + resource + " not found");

        return in;
    }
}
