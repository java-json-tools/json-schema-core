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

import com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.concurrent.Immutable;

/**
 * A specialized {@link ValueHolder} for values implementing {@link AsJson}
 *
 * @param <T> the type of the value
 */
@Immutable
final class AsJsonValueHolder<T extends AsJson>
    extends ValueHolder<T>
{
    AsJsonValueHolder(final String name, final T value)
    {
        super(name, value);
    }

    @Override
    protected JsonNode valueAsJson()
    {
        return value.asJson();
    }
}
