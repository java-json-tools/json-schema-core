package com.github.fge.jsonschema.load.transform;

import com.github.fge.jsonschema.util.MapBuilder;
import com.github.fge.jsonschema.util.URIUtils;

import java.net.URI;

final class PathRedirectMapBuilder
    extends MapBuilder<URI, URI>
{
    PathRedirectMapBuilder()
    {
        super(URIUtils.uriNormalizer(), URIUtils.pathURIChecker(),
            URIUtils.uriNormalizer(), URIUtils.pathURIChecker());
    }

    @Override
    protected void checkEntry(final URI key, final URI value)
    {
        BUNDLE.checkArgumentPrintf(!key.equals(value),
            "pathRedirect.selfRedirect", key);
    }
}
