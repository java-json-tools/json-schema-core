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
 * URI translation
 *
 * <p>A URI translation happens each time you load a schema from a URI, either
 * from user code or because of a JSON Reference encountered in a schema.</p>
 *
 * <p>You have three ways to alter URIs:</p>
 *
 * <ul>
 *     <li>resolve against a default namespace;</li>
 *     <li>perform path redirections;</li>
 *     <li>perform schema URI redirections.</li>
 * </ul>
 *
 * <p>See {@link com.github.fge.jsonschema.core.load.uri.URITranslatorConfigurationBuilder}
 * for more details.</p>
 */
package com.github.fge.jsonschema.core.load.uri;