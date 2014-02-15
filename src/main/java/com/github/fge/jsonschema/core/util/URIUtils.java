package com.github.fge.jsonschema.core.util;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Optional;

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

    /*
     * NORMALIZERS
     */
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

    private static final Function<URI, URI> URI_NORMALIZER
        = new Function<URI, URI>()
        {
            @Nullable
            @Override
            public URI apply(@Nullable final URI input)
            {
                if (input == null)
                    return null;

                final URI uri = input.normalize();

                final String scheme = uri.getScheme();
                final String host = uri.getHost();

                if (scheme == null && host == null)
                    return uri;

                final String userinfo = uri.getUserInfo();
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

    private static final Function<URI, URI> SCHEMAURI_NORMALIZER
        = new Function<URI, URI>()
    {
        @Nullable
        @Override
        public URI apply(@Nullable final URI input)
        {
            final URI uri = URI_NORMALIZER.apply(input);
            if (uri == null)
                return null;
            try {
                return new URI(uri.getScheme(), uri.getSchemeSpecificPart(),
                    Optional.fromNullable(uri.getFragment()).or(""));
            } catch (URISyntaxException e) {
                throw new RuntimeException("How did I get there??", e);
            }
        }
    };

    /*
     * CHECKERS
     */
    private static final ArgumentChecker<String> SCHEME_CHECKER
        = new ArgumentChecker<String>()
    {
        @Override
        public void check(final String argument)
        {
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

    private static final ArgumentChecker<URI> PATHURI_CHECKER
        = new ArgumentChecker<URI>()
    {
        @Override
        public void check(final URI argument)
        {
            BUNDLE.checkArgumentPrintf(argument.isAbsolute(),
                "uriChecks.notAbsolute", argument);
            BUNDLE.checkArgumentPrintf(argument.getFragment() == null,
                "uriChecks.fragmentNotNull", argument);
            BUNDLE.checkArgumentPrintf(argument.getQuery() == null,
                "uriChecks.queryNotNull", argument);
            BUNDLE.checkArgumentPrintf(argument.getPath().endsWith("/"),
                "uriChecks.noEndingSlash", argument);
        }
    };

    private static final ArgumentChecker<URI> SCHEMAURI_CHECKER
        = new ArgumentChecker<URI>()
    {
        @Override
        public void check(final URI argument)
        {
            BUNDLE.checkArgumentPrintf(argument.isAbsolute(),
                "uriChecks.notAbsolute", argument);
            final JsonRef ref = JsonRef.fromURI(argument);
            BUNDLE.checkArgumentPrintf(ref.isAbsolute(),
                "uriChecks.notAbsoluteRef", argument);
            BUNDLE.checkArgumentPrintf(!argument.getPath().endsWith("/"),
                "uriChecks.endingSlash", argument);
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
        return URI_NORMALIZER;
    }

    /**
     * Fully normalize a URI as per RFC 3986
     *
     * @param uri the URI
     * @return the fully normalized URI
     */
    public static URI normalizeURI(@Nullable final URI uri)
    {
        return URI_NORMALIZER.apply(uri);
    }

    public static Function<URI, URI> schemaURINormalizer()
    {
        return SCHEMAURI_NORMALIZER;
    }

    public static URI normalizeSchemaURI(@Nullable final URI uri)
    {
        return SCHEMAURI_NORMALIZER.apply(uri);
    }

    public static ArgumentChecker<String> schemeChecker()
    {
        return SCHEME_CHECKER;
    }

    public static void checkScheme(final String scheme)
    {
        SCHEME_CHECKER.check(scheme);
    }

    public static ArgumentChecker<URI> pathURIChecker()
    {
        return PATHURI_CHECKER;
    }

    public static void checkPathURI(final URI uri)
    {
        PATHURI_CHECKER.check(uri);
    }

    public static ArgumentChecker<URI> schemaURIChecker()
    {
        return SCHEMAURI_CHECKER;
    }

    public static void checkSchemaURI(final URI uri)
    {
        SCHEMAURI_CHECKER.check(uri);
    }
}
