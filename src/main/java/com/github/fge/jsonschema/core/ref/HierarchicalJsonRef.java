/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.ref;

import java.net.URI;

/**
 * JSON Reference for classical, hierarchical URIs
 *
 * <p>A hierarchical URI is defined as a URI which is either not absolute, or
 * which is absolute but not opaque. Resolution of such URIs can therefore
 * proceed as described in <a href="http://tools.ietf.org/html/rfc3986">RFC 3986
 * </a>.</p>
 *
 * <p>An example of URIs which are both absolute and opaque are jar URLs, which
 * have a dedicated class for this reason ({@link JarJsonRef}).</p>
 */
final class HierarchicalJsonRef
    extends JsonRef
{
    HierarchicalJsonRef(final URI uri)
    {
        super(uri);
    }

    @Override
    public boolean isAbsolute()
    {
        if (!legal)
            return false;
        return locator.isAbsolute() && pointer.isEmpty();
    }

    @Override
    public JsonRef resolve(final JsonRef other)
    {
        return fromURI(uri.resolve(other.uri));
    }
}
