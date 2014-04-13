/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
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
 * com.github.fge.jsonschema.core.tree.SchemaTree} and, in addition to navigation
 * capabilities, offers other information such as the current URI context
 * defined by that schema and JSON Reference resolution.</p>
 */
package com.github.fge.jsonschema.core.tree;
