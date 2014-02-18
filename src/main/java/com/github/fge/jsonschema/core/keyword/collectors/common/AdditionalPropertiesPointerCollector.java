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

package com.github.fge.jsonschema.core.keyword.collectors.common;

import com.github.fge.jackson.jsonpointer.JsonPointer;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.jsonschema.core.keyword.collectors.PointerCollector;
import com.github.fge.jsonschema.core.keyword.collectors.helpers.AbstractPointerCollector;

import java.util.Collection;

public final class AdditionalPropertiesPointerCollector
    extends AbstractPointerCollector
{
    private static final PointerCollector INSTANCE
        = new AdditionalPropertiesPointerCollector();

    private AdditionalPropertiesPointerCollector()
    {
        super("additionalProperties");
    }

    public static PointerCollector getInstance()
    {
        return INSTANCE;
    }

    @Override
    public void collect(final Collection<JsonPointer> pointers,
        final SchemaTree tree)
    {
        if (getNode(tree).isObject())
            pointers.add(basePointer);
    }
}
