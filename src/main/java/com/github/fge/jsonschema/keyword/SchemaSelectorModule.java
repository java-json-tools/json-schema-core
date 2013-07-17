package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;

import javax.inject.Provider;
import java.net.URI;
import java.util.Map;

@Beta
public class SchemaSelectorModule
    extends AbstractModule
    implements Provider<SchemaSelector>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final Map<URI, SchemaDescriptor> descriptors
        = Maps.newHashMap();
    private SchemaDescriptor defaultDescriptor;

    public SchemaSelectorModule()
    {
        defaultDescriptor = SchemaDescriptors.byDefault();

        SchemaDescriptor descriptor;

        descriptor = SchemaDescriptors.draftv3();
        descriptors.put(descriptor.getLocator(), descriptor);
        descriptor = SchemaDescriptors.draftv4();
        descriptors.put(descriptor.getLocator(), descriptor);
        descriptor = SchemaDescriptors.draftv4HyperSchema();
        descriptors.put(descriptor.getLocator(), descriptor);
    }

    protected final void setDefaultDescriptor(final SchemaDescriptor descriptor)
    {
        defaultDescriptor = BUNDLE.checkNotNull(descriptor,
            "schemaDescriptor.nullDescriptor");
    }

    protected final void addDescriptor(final SchemaDescriptor descriptor)
    {
        final URI uri = descriptor.getLocator();
        BUNDLE.checkArgumentPrintf(descriptors.put(uri, descriptor) == null,
            "schemaSelector.duplicateSchema", uri);
    }

    @Override
    protected final void configure()
    {
    }

    @Provides
    public final Map<URI, SchemaDescriptor> getDescriptors()
    {
        return ImmutableMap.copyOf(descriptors);
    }

    @Provides
    public final SchemaDescriptor getDefaultDescriptor()
    {
        return defaultDescriptor;
    }

    @Override
    public final SchemaSelector get()
    {
        final Map<URI, SchemaDescriptor> descriptorMap
            = ImmutableMap.copyOf(descriptors);
        return new SchemaSelector(descriptorMap, defaultDescriptor);
    }
}
