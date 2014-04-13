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

package com.github.fge.jsonschema.core.load.download;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

/**
 * Default URI downloader
 *
 * <p>{@link URL}'s API doc guarantees that an implementation can handle the
 * following schemes: {@code http}, {@code https}, {@code ftp}, {@code file}
 * and {@code jar}. This is what this downloader uses.</p>
 *
 * @see URL#openStream()
 */
public final class DefaultURIDownloader
    implements URIDownloader
{
    private static final URIDownloader INSTANCE
        = new DefaultURIDownloader();

    private DefaultURIDownloader()
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
        return source.toURL().openStream();
    }
}
