package com.github.fge.jsonschema.util;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

public final class URIUtils
{
    private static final Function<String, String> LOWERCASE
        = new Function<String, String>()
    {
        @Nullable
        @Override
        public String apply(@Nullable final String input)
        {
            return input == null ? null : input.toLowerCase();
        }
    };

    private URIUtils()
    {
    }

    public static Function<String, String> schemeNormalizer()
    {
        return LOWERCASE;
    }

    public static String normalizeScheme(final String scheme)
    {
        return LOWERCASE.apply(scheme);
    }

    public static Function<URI, URI> uriNormalizer()
    {
        return new Function<URI, URI>()
        {
            @Nullable
            @Override
            public URI apply(@Nullable final URI input)
            {
                if (input == null)
                    return null;

                final URI uri = input.normalize();

                final String scheme = uri.getScheme();
                final String userinfo = uri.getUserInfo();
                final String host = uri.getHost();
                final int port = uri.getPort();
                final String path = uri.getPath();
                final String query = uri.getQuery();
                final String fragment = uri.getFragment();

                try {
                    return new URI(LOWERCASE.apply(scheme), userinfo,
                        LOWERCASE.apply(host), port, path, query, fragment);
                } catch (URISyntaxException e) {
                    throw new IllegalStateException("How did I get there??", e);
                }
            }
        };
    }

    public static URI normalizeURI(final URI uri)
    {
        return uriNormalizer().apply(uri);
    }
}
