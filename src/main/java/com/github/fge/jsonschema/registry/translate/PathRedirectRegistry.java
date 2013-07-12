package com.github.fge.jsonschema.registry.translate;

import com.github.fge.jsonschema.util.Registry;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
final class PathRedirectRegistry
    extends Registry<URI, URI>
{
    PathRedirectRegistry()
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
