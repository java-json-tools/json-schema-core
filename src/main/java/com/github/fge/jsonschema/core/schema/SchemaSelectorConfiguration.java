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
