/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.schema;

import com.github.fge.Frozen;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public final class SchemaSelectorConfiguration
    implements Frozen<SchemaSelectorConfigurationBuilder>
{
    final Map<URI, SchemaDescriptor> descriptors = Maps.newHashMap();

    SchemaDescriptor defaultDescriptor;

    public static SchemaSelectorConfigurationBuilder newBuilder()
    {
        return new SchemaSelectorConfigurationBuilder();
    }

    public static SchemaSelectorConfiguration byDefault()
    {
        return newBuilder().freeze();
    }

    SchemaSelectorConfiguration(
        final SchemaSelectorConfigurationBuilder builder)
    {
        defaultDescriptor = builder.defaultDescriptor;
        descriptors.putAll(builder.descriptors);
    }

    public Map<URI, SchemaDescriptor> getDescriptors()
    {
        return ImmutableMap.copyOf(descriptors);
    }

    public SchemaDescriptor getDefaultDescriptor()
    {
        return defaultDescriptor;
    }

    @Override
    public SchemaSelectorConfigurationBuilder thaw()
    {
        return new SchemaSelectorConfigurationBuilder(this);
    }
}
