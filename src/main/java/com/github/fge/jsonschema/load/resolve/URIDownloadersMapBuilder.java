package com.github.fge.jsonschema.load.resolve;

import com.github.fge.jsonschema.load.DefaultURIDownloader;
import com.github.fge.jsonschema.load.ResourceURIDownloader;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.util.ArgumentChecker;
import com.github.fge.jsonschema.util.MapBuilder;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@Beta
public class URIDownloadersMapBuilder
    extends MapBuilder<String, URIDownloader>
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

    public URIDownloadersMapBuilder()
    {
        super(URIUtils.schemeNormalizer(), URIUtils.schemeChecker(),
            Functions.<URIDownloader>identity(),
            ArgumentChecker.<URIDownloader>anythingGoes());
        putAll(DEFAULT_DOWNLOADERS);
    }

    @Override
    protected final void checkEntry(final String key, final URIDownloader value)
    {
    }
}
