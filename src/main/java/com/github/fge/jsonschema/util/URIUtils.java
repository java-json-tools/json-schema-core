package com.github.fge.jsonschema.util;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.CharMatcher;
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
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /*
     * ASCII letters, and whatever is legal in a URI scheme
     */
    private static final CharMatcher ALPHA;
    private static final CharMatcher SCHEME_LEGAL;

    static {
        ALPHA = CharMatcher.inRange('a', 'z')
            .or(CharMatcher.inRange('A', 'Z')).precomputed();
        SCHEME_LEGAL = ALPHA.or(CharMatcher.inRange('0', '9'))
            .or(CharMatcher.anyOf("+-.")).precomputed();
    }

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

    private static final ArgumentChecker<String> SCHEME_CHECKER
        = new ArgumentChecker<String>()
    {
        @Override
        public void check(@Nullable final String argument)
        {
            BUNDLE.checkNotNull("loadingCfg.nullScheme", argument);
            final String errmsg = BUNDLE.printf("loadingCfg.illegalScheme",
                argument);
            if (argument.isEmpty())
                throw new IllegalArgumentException(errmsg);
            if (!ALPHA.matches(argument.charAt(0)))
                throw new IllegalArgumentException(errmsg);
            if (!SCHEME_LEGAL.matchesAllOf(argument))
                throw new IllegalArgumentException(errmsg);
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

    public static ArgumentChecker<String> schemeChecker()
    {
        return SCHEME_CHECKER;
    }

    public static void checkScheme(final String scheme)
    {
        schemeChecker().check(scheme);
    }

}
