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
import com.github.fge.jsonschema.core.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
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
