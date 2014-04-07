/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.util;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.ref.JsonRef;
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
 *
 * <p>It also contains {@link ArgumentChecker}s to detect whether URIs are well
 * formed given a defined requirement, and static methods calling these
 * argument checkers as well.</p>
 *
 * @see ArgumentChecker
 * @see Function
 * @see Registry
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
            BUNDLE.checkArgumentPrintf(argument.getPath() != null,
                "uriChecks.noPath", argument);
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
     * Full URI normalizer, as per RFC 3986
     *
     * @return a full URI normalizer as a {@link Function}
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
     *
     * @see #uriNormalizer()
     */
    public static URI normalizeURI(@Nullable final URI uri)
    {
        return URI_NORMALIZER.apply(uri);
    }

    /**
     * Schema URI normalizer
     *
     * <p>This performs the same normalization as {@link #uriNormalizer()},
     * except it will also append an empty fragment if no fragment is present.
     * </p>
     *
     * @return a schema URI normalizer as a {@link Function}
     */
    public static Function<URI, URI> schemaURINormalizer()
    {
        return SCHEMAURI_NORMALIZER;
    }

    /**
     * Perform schema URI normalization
     *
     * @param uri the URI
     * @return the normalized schema URI
     *
     * @see #schemaURINormalizer()
     */
    public static URI normalizeSchemaURI(@Nullable final URI uri)
    {
        return SCHEMAURI_NORMALIZER.apply(uri);
    }

    /**
     * Return an argument checker to check the correctness of a URI scheme
     *
     * @return an argument checker
     */
    public static ArgumentChecker<String> schemeChecker()
    {
        return SCHEME_CHECKER;
    }

    /**
     * Check whether a given string as an argument is a legal URI scheme
     *
     * @param scheme the string to check
     *
     * @see #schemeChecker()
     */
    public static void checkScheme(final String scheme)
    {
        SCHEME_CHECKER.check(scheme);
    }

    /**
     * Argument checker to check whether a URI is a valid path URI
     *
     * <p>A URI is a valid path URI if all the following conditions are met:</p>
     *
     * <ul>
     *     <li>it is absolute;</li>
     *     <li>it is hierarchical;</li>
     *     <li>it has no fragment part;</li>
     *     <li>it has a non-empty path component, and this path component ends
     *     with a {@code /}.</li>
     * </ul>
     *
     * @return an argument checker
     */
    public static ArgumentChecker<URI> pathURIChecker()
    {
        return PATHURI_CHECKER;
    }

    /**
     * Check whether a URI is a valid path URI
     *
     * @param uri the URI to check
     *
     * @see #pathURIChecker()
     */
    public static void checkPathURI(final URI uri)
    {
        PATHURI_CHECKER.check(uri);
    }

    /**
     * Argument checker for a schema URI
     *
     * <p>A URI is a valid schema URI if all of the following conditions are
     * true:</p>
     *
     * <ul>
     *     <li>the URI is absolute;</li>
     *     <li>it has no fragment part, or its fragment part is empty.</li>
     * </ul>
     *
     * @return an argument checker
     */
    public static ArgumentChecker<URI> schemaURIChecker()
    {
        return SCHEMAURI_CHECKER;
    }

    /**
     * Check that a URI is a valid schema URI
     *
     * @param uri the URI to check
     *
     * @see #schemaURIChecker()
     */
    public static void checkSchemaURI(final URI uri)
    {
        SCHEMAURI_CHECKER.check(uri);
    }
}
