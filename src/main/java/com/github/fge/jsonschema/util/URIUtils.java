package com.github.fge.jsonschema.util;

import com.google.common.base.Function;

import javax.annotation.Nullable;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utility class for URI normalization
 *
 * <p>RFC 3986, sections 3.1 and 3.2.2, says that normalized schemes and
 * hostnames are normalized to lowercase; unfortunately, Java's {@link
 * URI#normalize()} does not go as far as the RFC says, and leaves the scheme
 * and host parts of the URI intact.</p>
 *
 * <p>This class provides methods to fully normalize both schemes and URIs,
 * and {@link Function}s to perform these normalizations.</p>
 */
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

    /**
     * Return a {@link Function} performing URI scheme normalization, as per RFC
     * 3986, section 3.1
     *
     * @return a function
     */
    public static Function<String, String> schemeNormalizer()
    {
        return LOWERCASE;
    }

    /**
     * Normalize a scheme according to RFC 3986, section 3.1
     *
     * @param scheme the scheme
     * @return the normalized scheme
     */
    public static String normalizeScheme(@Nullable final String scheme)
    {
        return LOWERCASE.apply(scheme);
    }

    /**
     * Return a {@link Function} performing full URI normalization, as per RFC
     * 3986
     *
     * @return a function
     */
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

    /**
     * Fully normalize a URI as per RFC 3986
     *
     * @param uri the URI
     * @return the fully normalized URI
     */
    public static URI normalizeURI(@Nullable final URI uri)
    {
        return uriNormalizer().apply(uri);
    }
}
