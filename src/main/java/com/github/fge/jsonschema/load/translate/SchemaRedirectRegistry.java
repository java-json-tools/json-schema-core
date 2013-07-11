package com.github.fge.jsonschema.load.translate;

import com.github.fge.jsonschema.util.Registry;
import com.github.fge.jsonschema.util.URIUtils;
import com.google.common.annotations.Beta;

import java.net.URI;

@Beta
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
