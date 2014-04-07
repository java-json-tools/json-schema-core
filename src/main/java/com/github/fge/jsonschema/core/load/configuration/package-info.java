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
 * Schema loading configuration
 *
 * <p>Many aspects of schema loading are configurable.</p>
 *
 * <p>For starters, you can configure your loader to read even malformed JSON
 * inputs. You can therefore have schemas with comments etc, even though JSON
 * normally does not allow them. This is done using Jackson's parser features.
 * </p>
 *
 * <p>You can also configure the way schema loading will resolve URIs to your
 * schemas: set a default URI namespace, silently redirect schema URIs to other
 * URIs or even whole URI hierarchies. You can also add or remove support for
 * URI schemes.</p>
 *
 * <p>Finally, this is also where you decide whether you will use canonical
 * dereferencing or inline dereferencing. The author recommends the former!</p>
 *
 * <p>The default loading configuration (obtained using {@link
 * com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration#byDefault()})
 * will use the default set of supported URI schemes</p>
 */
package com.github.fge.jsonschema.core.load.configuration;

