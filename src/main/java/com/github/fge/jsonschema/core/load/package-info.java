/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

/**
 * Schema loading and JSON Reference resolving
 *
 * <p>This package contains all components necessary to load and preload JSON
 * schemas, along with the processor in charge of JSON Reference resolving
 * ({@link com.github.fge.jsonschema.core.load.RefResolver}).</p>
 *
 * <p>The main loading class is {@link
 * com.github.fge.jsonschema.core.load.SchemaLoader}. It relies on downloaders
 * configured in a {@link com.github.fge.jsonschema.core.load.URIManager} to
 * load schemas it does not already know of.</p>
 *
 * <p>Note that you can configure the latter to support an arbitrary set of URI
 * schemes, or remove support for schemes you don't want to support (for
 * security reasons or otherwise).</p>
 *
 * <p>Configuring schema loading is done using a {@link
 * com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration}. URI
 * resolving and loading is done using a {@link
 * com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration}.</p>
 */
package com.github.fge.jsonschema.core.load;
