package com.github.fge.jsonschema.load.transform;

import com.github.fge.jsonschema.util.Registry;
import com.github.fge.jsonschema.util.URIUtils;

import java.net.URI;

final class SchemaRedirectRegistry
    extends Registry<URI, URI>
{
    SchemaRedirectRegistry()
    {
        super(URIUtils.schemaURINormalizer(), URIUtils.schemaURIChecker(),
            URIUtils.schemaURINormalizer(), URIUtils.schemaURIChecker());
    }

    @Override
    protected void checkEntry(final URI key, final URI value)
    {
        BUNDLE.checkArgumentPrintf(!key.equals(value),
            "schemaRedirect.selfRedirect", key);
    }
}
