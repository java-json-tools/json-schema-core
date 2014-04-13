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

package com.github.fge.jsonschema.core.load.configuration;

import com.github.fge.jsonschema.core.load.download.DefaultURIDownloader;
import com.github.fge.jsonschema.core.load.download.ResourceURIDownloader;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.util.ArgumentChecker;
import com.github.fge.jsonschema.core.util.Registry;
import com.github.fge.jsonschema.core.util.URIUtils;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

final class URIDownloadersRegistry
    extends Registry<String, URIDownloader>
{
    private static final Map<String, URIDownloader> DEFAULT_DOWNLOADERS;

    static {
        final ImmutableMap.Builder<String, URIDownloader> builder
            = ImmutableMap.builder();

        String scheme;
        URIDownloader downloader;

        scheme = "http";
        downloader = DefaultURIDownloader.getInstance();
        builder.put(scheme, downloader);

        scheme = "https";
        downloader = DefaultURIDownloader.getInstance();
        builder.put(scheme, downloader);

        scheme = "file";
        downloader = DefaultURIDownloader.getInstance();
        builder.put(scheme, downloader);

        scheme = "ftp";
        downloader = DefaultURIDownloader.getInstance();
        builder.put(scheme, downloader);

        scheme = "jar";
        downloader = DefaultURIDownloader.getInstance();
        builder.put(scheme, downloader);

        scheme = "resource";
        downloader = ResourceURIDownloader.getInstance();
        builder.put(scheme, downloader);

        DEFAULT_DOWNLOADERS = builder.build();
    }

    public URIDownloadersRegistry()
    {
        super(URIUtils.schemeNormalizer(), URIUtils.schemeChecker(),
            Functions.<URIDownloader>identity(),
            ArgumentChecker.<URIDownloader>anythingGoes());
        putAll(DEFAULT_DOWNLOADERS);
    }

    @Override
    protected void checkEntry(final String key, final URIDownloader value)
    {
    }
}
