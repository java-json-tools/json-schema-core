package com.github.fge.jsonschema.keyword;

import com.github.fge.Frozen;
import com.github.fge.jsonschema.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.walk.collectors.PointerCollector;
import com.google.common.collect.ImmutableMap;

import java.net.URI;
import java.util.Map;
import java.util.Set;

public final class SchemaDescriptor
    implements Frozen<SchemaDescriptorBuilder>
{
    final URI locator;
    final Map<String, KeywordDescriptor> keywords;
    private final Map<String, SyntaxChecker> syntaxCheckers;
    private final Map<String, PointerCollector> pointerCollectors;

    public static SchemaDescriptorBuilder newBuilder()
    {
        return new SchemaDescriptorBuilder();
    }

    SchemaDescriptor(final SchemaDescriptorBuilder builder)
    {
        locator = builder.locator;
        keywords = ImmutableMap.copyOf(builder.keywords);

        final ImmutableMap.Builder<String, SyntaxChecker> checkerBuilder
            = ImmutableMap.builder();
        final ImmutableMap.Builder<String, PointerCollector> collectorBuilder
            = ImmutableMap.builder();

        String name;
        SyntaxChecker checker;
        PointerCollector collector;

        for (final Map.Entry<String, KeywordDescriptor> entry:
            keywords.entrySet()) {
            name = entry.getKey();
            checker = entry.getValue().getSyntaxChecker();
            collector = entry.getValue().getPointerCollector();
            if (checker != null)
                checkerBuilder.put(name, checker);
            if (collector != null)
                collectorBuilder.put(name, collector);
        }

        syntaxCheckers = checkerBuilder.build();
        pointerCollectors = collectorBuilder.build();
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
        return syntaxCheckers;
    }

    public Map<String, PointerCollector> getPointerCollectors()
    {
        return pointerCollectors;
    }

    @Override
    public SchemaDescriptorBuilder thaw()
    {
        return new SchemaDescriptorBuilder(this);
    }
}
