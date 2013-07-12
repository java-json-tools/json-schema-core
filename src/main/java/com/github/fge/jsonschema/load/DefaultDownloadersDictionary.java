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

package com.github.fge.jsonschema.load;

import com.github.fge.jsonschema.library.Dictionary;
import com.github.fge.jsonschema.library.DictionaryBuilder;
import com.github.fge.jsonschema.loader.resolve.DefaultURIDownloader;
import com.github.fge.jsonschema.loader.resolve.ResourceURIDownloader;
import com.github.fge.jsonschema.loader.resolve.URIDownloader;
import com.github.fge.jsonschema.loader.resolve.URIDownloadersRegistry;

/**
 * Dictionary of default supported URI schemes
 *
 * <p>The set of default supported schemes is:</p>
 *
 * <ul>
 *     <li>{@code http}</li>;
 *     <li>{@code https}</li>;
 *     <li>{@code file}</li>;
 *     <li>{@code ftp}</li>;
 *     <li>{@code jar}</li>;
 *     <li>{@code resource}</li>.
 * </ul>
 *
 * @see DefaultURIDownloader
 * @see ResourceURIDownloader
 *
 * @deprecated use {@link URIDownloadersRegistry} instead. Will be removed in
 * 1.1.10.
 */
@Deprecated
public final class DefaultDownloadersDictionary
{
    private static final Dictionary<URIDownloader> DICTIONARY;

    private DefaultDownloadersDictionary()
    {
    }

    static {
        final DictionaryBuilder<URIDownloader> builder
            = Dictionary.newBuilder();

        String scheme;
        URIDownloader downloader;

        scheme = "http";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "https";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "file";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "ftp";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "jar";
        downloader = DefaultURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        scheme = "resource";
        downloader = ResourceURIDownloader.getInstance();
        builder.addEntry(scheme, downloader);

        DICTIONARY = builder.freeze();
    }

    /**
     * Get the dictionary of downloaders
     *
     * @return a dictionary
     */
    public static Dictionary<URIDownloader> get()
    {
        return DICTIONARY;
    }
}
