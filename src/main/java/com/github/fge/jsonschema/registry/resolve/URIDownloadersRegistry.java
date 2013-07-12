package com.github.fge.jsonschema.registry.resolve;

import com.github.fge.jsonschema.util.ArgumentChecker;
import com.github.fge.jsonschema.util.Registry;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;
import com.google.common.base.Functions;
import com.google.common.collect.ImmutableMap;

import java.util.Map;

@Beta
public final class URIDownloadersRegistry
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
