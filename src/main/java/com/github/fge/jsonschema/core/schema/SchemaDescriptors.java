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

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.keyword.KeywordDescriptor;
import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.keyword.syntax.checkers.SyntaxChecker;
import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV3SyntaxCheckerDictionary;
import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV4HyperSchemaSyntaxCheckerDictionary;
import com.github.fge.jsonschema.core.keyword.syntax.dictionaries.DraftV4SyntaxCheckerDictionary;
import com.github.fge.jsonschema.core.keyword.collectors.DraftV3PointerCollectorDictionary;
import com.github.fge.jsonschema.core.keyword.collectors.DraftV4PointerCollectorDictionary;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.google.common.annotations.Beta;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;

@Beta
public final class SchemaDescriptors
{
    private static final SchemaDescriptor DRAFTV4
        = draftv4Descriptor();
    private static final SchemaDescriptor DRAFTV4_HYPERSCHEMA
        = draftv4HyperSchemaDescriptor();
    private static final SchemaDescriptor DRAFTV3
        = draftv3Descriptor();
    private static final SchemaDescriptor DEFAULT = DRAFTV4;


    private SchemaDescriptors()
    {
    }

    public static SchemaDescriptor draftv4()
    {
        return DRAFTV4;
    }

    public static SchemaDescriptor draftv4HyperSchema()
    {
        return DRAFTV4_HYPERSCHEMA;
    }

    public static SchemaDescriptor draftv3()
    {
        return DRAFTV3;
    }

    public static SchemaDescriptor byDefault()
    {
        return DEFAULT;
    }

    private static SchemaDescriptor draftv4Descriptor()
    {
        final SchemaDescriptorBuilder builder = SchemaDescriptor.newBuilder();
        final List<KeywordDescriptor> list
            = mergeDicts(DraftV4SyntaxCheckerDictionary.get(),
            DraftV4PointerCollectorDictionary.get());

        builder.setLocator(SchemaVersion.DRAFTV4.getLocation());
        for (final KeywordDescriptor descriptor: list)
            builder.addKeyword(descriptor);
        return builder.freeze();
    }

    private static SchemaDescriptor draftv4HyperSchemaDescriptor()
    {
        final SchemaDescriptorBuilder builder = SchemaDescriptor.newBuilder();
        final List<KeywordDescriptor> list
            = mergeDicts(DraftV4HyperSchemaSyntaxCheckerDictionary.get(),
            DraftV4PointerCollectorDictionary.get());

        builder.setLocator(SchemaVersion.DRAFTV4_HYPERSCHEMA.getLocation());
        for (final KeywordDescriptor descriptor: list)
            builder.addKeyword(descriptor);
        return builder.freeze();
    }

    private static SchemaDescriptor draftv3Descriptor()
    {
        final SchemaDescriptorBuilder builder = SchemaDescriptor.newBuilder();
        final List<KeywordDescriptor> list
            = mergeDicts(DraftV3SyntaxCheckerDictionary.get(),
            DraftV3PointerCollectorDictionary.get());

        builder.setLocator(SchemaVersion.DRAFTV3.getLocation());
        for (final KeywordDescriptor descriptor: list)
            builder.addKeyword(descriptor);
        return builder.freeze();
    }

    private static List<KeywordDescriptor> mergeDicts(
        final Dictionary<SyntaxChecker> checkers,
        final Dictionary<PointerCollector> collectors
    )
    {
        final Map<String, KeywordDescriptor.Builder> map
            = Maps.newHashMap();

        String name;
        KeywordDescriptor.Builder builder;

        for (final Map.Entry<String, SyntaxChecker> entry:
            checkers.entries().entrySet()) {
            name = entry.getKey();
            builder = map.get(name);
            if (builder == null) {
                builder = KeywordDescriptor.withName(name);
                map.put(name, builder);
            }
            builder.setSyntaxChecker(entry.getValue());
        }

        for (final Map.Entry<String, PointerCollector> entry:
            collectors.entries().entrySet()) {
            name = entry.getKey();
            builder = map.get(name);
            if (builder == null) {
                builder = KeywordDescriptor.withName(name);
                map.put(name, builder);
            }
            builder.setPointerCollector(entry.getValue());
        }

        final ImmutableList.Builder<KeywordDescriptor> listBuilder
            = ImmutableList.builder();

        for (final KeywordDescriptor.Builder k: map.values())
            listBuilder.add(k.build());

        return listBuilder.build();
    }
}
