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
 * Schema walker, listener and processor
 *
 * <p>Using classes in this package, you can create a {@link
 * com.github.fge.jsonschema.walk.SchemaListener} instance, attach it to a
 * {@link com.github.fge.jsonschema.walk.SchemaWalker} and generate a custom
 * product out of this listening process.</p>
 *
 * <p>You can also use a walker as a processor, namely {@link
 * com.github.fge.jsonschema.walk.SchemaWalkerProcessor}, in which case you must
 * provide a {@link com.github.fge.jsonschema.walk.SchemaListenerProvider} as
 * well, which will generate a new listener for each processed value, and return
 * a {@link com.github.fge.jsonschema.util.ValueHolder} holding the product of
 * the created listener.</p>
 *
 * <p>Important node: <b>it is supposed that the schema is valid.</b></p>
 */
package com.github.fge.jsonschema.load;
