package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

@Beta
public class SchemaSelectorModule
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

    protected final void setDefaultVersion(final SchemaVersion version)
    {
        BUNDLE.checkNotNull(version, "schemaSelector.nullVersion");
        defaultDescriptor = descriptors.get(version.getLocation());
    }

    protected final void addDescriptor(final SchemaDescriptor descriptor,
        final boolean makeDefault)
    {
        final URI uri = BUNDLE.checkNotNull(descriptor,
            "schemaSelector.nullDescriptor").getLocator();
        BUNDLE.checkArgumentPrintf(descriptors.put(uri, descriptor) == null,
            "schemaSelector.duplicateSchema", uri);
        if (makeDefault)
            defaultDescriptor = descriptor;
    }

    public final Map<URI, SchemaDescriptor> getDescriptors()
    {
        return ImmutableMap.copyOf(descriptors);
    }

    public final SchemaDescriptor getDefaultDescriptor()
    {
        return defaultDescriptor;
    }

    public final SchemaSelector get()
    {
        final Map<URI, SchemaDescriptor> descriptorMap
            = ImmutableMap.copyOf(descriptors);
        return new SchemaSelector(descriptorMap, defaultDescriptor);
    }
}
