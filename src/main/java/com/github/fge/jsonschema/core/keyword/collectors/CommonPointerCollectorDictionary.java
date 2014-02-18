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

package com.github.fge.jsonschema.core.keyword.collectors;

import com.github.fge.jsonschema.core.util.Dictionary;
import com.github.fge.jsonschema.core.util.DictionaryBuilder;
import com.github.fge.jsonschema.core.keyword.collectors.common.AdditionalItemsPointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.common.AdditionalPropertiesPointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.common.DependenciesPointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.helpers.SchemaMapPointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.helpers.SchemaOrSchemaArrayPointerCollector;

/**
 * Dictionary of pointer collectors common to both draft v4 and draft v3
 */
public final class CommonPointerCollectorDictionary
{
    private static final Dictionary<PointerCollector> DICTIONARY;

    private CommonPointerCollectorDictionary()
    {
    }

    static {
        final DictionaryBuilder<PointerCollector> builder
            = Dictionary.newBuilder();

        String keyword;
        PointerCollector collector;

        keyword = "additionalItems";
        collector = AdditionalItemsPointerCollector.getInstance();
        builder.addEntry(keyword, collector);

        keyword = "items";
        collector = new SchemaOrSchemaArrayPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        keyword = "additionalProperties";
        collector = AdditionalPropertiesPointerCollector.getInstance();
        builder.addEntry(keyword, collector);

        keyword = "properties";
        collector = new SchemaMapPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        keyword = "patternProperties";
        collector = new SchemaMapPointerCollector(keyword);
        builder.addEntry(keyword, collector);

        keyword = "dependencies";
        collector = DependenciesPointerCollector.getInstance();
        builder.addEntry(keyword, collector);

        DICTIONARY = builder.freeze();
    }

    public static Dictionary<PointerCollector> get()
    {
        return DICTIONARY;
    }
}
