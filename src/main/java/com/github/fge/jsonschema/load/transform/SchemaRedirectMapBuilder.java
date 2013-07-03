package com.github.fge.jsonschema.load.transform;

import com.github.fge.jsonschema.util.MapBuilder;
import com.github.fge.jsonschema.util.URIUtils;

import java.net.URI;

final class SchemaRedirectMapBuilder
    extends MapBuilder<URI, URI>
{
    SchemaRedirectMapBuilder()
    {
        super(URIUtils.schemaURINormalizer(), URIUtils.schemaURIChecker(),
            URIUtils.schemaURINormalizer(), URIUtils.schemaURIChecker());
    }

    @Override
    protected void checkEntry(final URI key, final URI value)
    {
        BUNDLE.checkArgumentPrintf(!key.equals(value),
            "pathRedirect.selfRedirect", key);
    }
}
