package com.github.fge.jsonschema.core.schema;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

public final class SchemaSelectorConfigurationBuilder
    implements Thawed<SchemaSelectorConfiguration>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    final Map<URI, SchemaDescriptor> descriptors = Maps.newHashMap();

    SchemaDescriptor defaultDescriptor;

    SchemaSelectorConfigurationBuilder(final SchemaSelectorConfiguration cfg)
    {
        descriptors.putAll(cfg.descriptors);
        defaultDescriptor = cfg.defaultDescriptor;
    }

    SchemaSelectorConfigurationBuilder()
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

    public SchemaSelectorConfigurationBuilder setDefaultVersion(
        final SchemaVersion version)
    {
        BUNDLE.checkNotNull(version, "schemaSelector.nullVersion");
        defaultDescriptor = descriptors.get(version.getLocation());
        return this;
    }

    public SchemaSelectorConfigurationBuilder addDescriptor(
        final SchemaDescriptor descriptor, final boolean makeDefault)
    {
        final URI uri = BUNDLE.checkNotNull(descriptor,
            "schemaSelector.nullDescriptor").getLocator();
        BUNDLE.checkArgumentPrintf(descriptors.put(uri, descriptor) == null,
            "schemaSelector.duplicateSchema", uri);
        if (makeDefault)
            defaultDescriptor = descriptor;
        return this;
    }

    @Override
    public SchemaSelectorConfiguration freeze()
    {
        return new SchemaSelectorConfiguration(this);
    }
}
