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

package com.github.fge.jsonschema.core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jsonschema.tree.SchemaTree;

/**
 * Interface implemented by classes having a JSON representation
 *
 * <p>This representation needs not be complete. For instance, {@link
 * SchemaTree} implements it to provide an object with the summary of its main
 * characteristics (loading URI, current pointer).</p>
 */
public interface AsJson
{
    /**
     * Return a JSON representation of this object
     *
     * @return a {@link JsonNode}
     */
    JsonNode asJson();
}
