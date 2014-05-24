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
 * JSON Schema keys (unique identifiers)
 *
 * <p>Classes in this package are closely related to {@link
 * com.github.fge.jsonschema.core.tree.SchemaTree} instances; one schema tree
 * will have one {@link com.github.fge.jsonschema.core.tree.key.SchemaKey}.</p>
 *
 * <p>Depending on the way you will have loaded your schema, the schema will be
 * either {@link com.github.fge.jsonschema.core.tree.key.AnonymousSchemaKey
 * anonymous} or {@link com.github.fge.jsonschema.core.tree.key.JsonRefSchemaKey
 * linked to a URI}.</p>
 *
 * <p>The fundamental difference between these two types of trees is that when
 * using an anonymous schema, all {@link
 * com.github.fge.jsonschema.core.ref.JsonRef JSON Reference}s found in a
 * non-anonymous schema tree will be resolved against this tree's location; if
 * the schema is anonymous, references will be resolved against the empyt URI
 * reference (which leads to the reference itself being returned).</p>
 */
package com.github.fge.jsonschema.core.tree.key;