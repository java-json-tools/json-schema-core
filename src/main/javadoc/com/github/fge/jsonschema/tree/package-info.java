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

/**
 * Navigable JSON tree representations
 *
 * <p>Classes in this package are wrappers over {@link
 * com.fasterxml.jackson.databind.JsonNode} instances with navigation
 * capabilities (using {@link
 * com.github.fge.jackson.jsonpointer.JsonPointer}).</p>
 *
 * <p>A JSON Schema is represented by a {@link
 * com.github.fge.jsonschema.tree.SchemaTree} and, in addition to navigation
 * capabilities, offers other information such as the current URI context
 * defined by that schema and JSON Reference resolution.</p>
 */
package com.github.fge.jsonschema.tree;
