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
 * provide both a {@link com.github.fge.jsonschema.walk.SchemaWalkerFactory}
 * and a {@link com.github.fge.jsonschema.walk.SchemaListenerFactory}. A new
 * walker and listener will be created for each call to {@code .process()}, and
 * the product of the listener will be returned wrappted into a {@link
 * com.github.fge.jsonschema.util.ValueHolder}.</p>
 */
package com.github.fge.jsonschema.walk;
