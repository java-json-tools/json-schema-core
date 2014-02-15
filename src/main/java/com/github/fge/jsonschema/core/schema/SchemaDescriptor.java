package com.github.fge.jsonschema.core.schema;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.core.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;
import java.util.Set;

@Beta
public final class SchemaDescriptor
    implements Frozen<SchemaDescriptorBuilder>
{
    final URI locator;
    final Map<String, KeywordDescriptor> keywords;

    public static SchemaDescriptorBuilder newBuilder()
    {
        return new SchemaDescriptorBuilder();
    }

    SchemaDescriptor(final SchemaDescriptorBuilder builder)
    {
        locator = builder.locator;
        keywords = ImmutableMap.copyOf(builder.keywords);
    }

    public URI getLocator()
    {
        return locator;
    }

    public Set<String> getSupportedKeywords()
    {
        return keywords.keySet();
    }

    public Map<String, SyntaxChecker> getSyntaxCheckers()
    {
        final ImmutableMap.Builder<String, SyntaxChecker> builder
            = ImmutableMap.builder();

        SyntaxChecker checker;

        for (final Map.Entry<String, KeywordDescriptor> entry:
            keywords.entrySet()) {
            checker = entry.getValue().getSyntaxChecker();
            if (checker != null)
                builder.put(entry.getKey(), checker);
        }

        return builder.build();
    }

    public Map<String, PointerCollector> getPointerCollectors()
    {
        final ImmutableMap.Builder<String, PointerCollector> builder
            = ImmutableMap.builder();

        PointerCollector collector;

        for (final Map.Entry<String, KeywordDescriptor> entry:
            keywords.entrySet()) {
            collector = entry.getValue().getPointerCollector();
            if (collector != null)
                builder.put(entry.getKey(), collector);
        }

        return builder.build();
    }

    @Override
    public SchemaDescriptorBuilder thaw()
    {
        return new SchemaDescriptorBuilder(this);
    }

    @Override
    public String toString()
    {
        return locator + " (" + keywords.size() + " keywords)";
    }
}
