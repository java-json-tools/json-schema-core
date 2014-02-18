/*
 * Copyright (c) 2013, Francis Galiegue <fgaliegue@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the Lesser GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * Lesser GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
