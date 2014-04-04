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
 * Core processing interface and building utility classes
 *
 * <p>The {@link com.github.fge.jsonschema.core.processing.Processor} interface is
 * the reason this whole library was created in the first place. While its name
 * has "json-schema" in it, the infrastructure provided by this package can be
 * used for totally different purposes.</p>
 *
 * <p>Apart from the core interface itself, there are also helpers for caching
 * results and combining processors together to create your own, customized
 * processing chains.</p>
 */
package com.github.fge.jsonschema.core.processing;
