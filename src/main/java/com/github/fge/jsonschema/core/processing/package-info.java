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
