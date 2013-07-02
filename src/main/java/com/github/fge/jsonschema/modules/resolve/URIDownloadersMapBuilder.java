package com.github.fge.jsonschema.modules.resolve;

import com.github.fge.jsonschema.load.DefaultDownloadersDictionary;
import com.github.fge.jsonschema.load.URIDownloader;
import com.github.fge.jsonschema.util.ArgumentChecker;
import com.github.fge.jsonschema.util.MapBuilder;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;
import com.google.common.base.Functions;

@Beta
public class URIDownloadersMapBuilder
    extends MapBuilder<String, URIDownloader>
{
    public URIDownloadersMapBuilder()
    {
        super(URIUtils.schemeNormalizer(), URIUtils.schemeChecker(),
            Functions.<URIDownloader>identity(),
            ArgumentChecker.<URIDownloader>anythingGoes());
        putAll(DefaultDownloadersDictionary.get().entries());
    }

    @Override
    protected final void checkEntry(final String key, final URIDownloader value)
    {
    }
}
