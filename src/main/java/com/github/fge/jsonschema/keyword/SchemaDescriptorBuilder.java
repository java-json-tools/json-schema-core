package com.github.fge.jsonschema.keyword;

import com.github.fge.Thawed;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.util.URIUtils;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;
import com.google.common.collect.Maps;

import java.net.URI;
import java.util.Map;

@Beta
public final class SchemaDescriptorBuilder
    implements Thawed<SchemaDescriptor>
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    URI locator;
    final Map<String, KeywordDescriptor> keywords = Maps.newHashMap();

    SchemaDescriptorBuilder()
    {
    }

    SchemaDescriptorBuilder(final SchemaDescriptor descriptor)
    {
        locator = descriptor.locator;
        keywords.putAll(descriptor.keywords);
    }

    public SchemaDescriptorBuilder setLocator(final URI uri)
    {
        BUNDLE.checkNotNull(uri, "schemaDescriptor.nullLocator");
        final URI normalized = URIUtils.toSchemaURI(uri);
        URIUtils.checkSchemaURI(normalized);
        locator = normalized;
        return this;
    }

    public SchemaDescriptorBuilder addKeyword(
        final KeywordDescriptor descriptor)
    {
        BUNDLE.checkNotNull(descriptor, "schemaDescriptor.nullDescriptor");
        keywords.put(descriptor.getName(), descriptor);
        return this;
    }

    public SchemaDescriptorBuilder removeKeyword(final String name)
    {
        keywords.remove(name);
        return this;
    }

    @Override
    public SchemaDescriptor freeze()
    {
        BUNDLE.checkNotNull(locator, "schemaDescriptor.nullDescriptor");
        return new SchemaDescriptor(this);
    }
}
