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
 * Jackson utility classes
 *
 * <p>{@link com.github.fge.jackson.JsonLoader} contains various
 * methods to load JSON documents as {@link
 * com.fasterxml.jackson.databind.JsonNode}s.You will also use to want {@link
 * com.github.fge.jackson.JacksonUtils} to grab a node factory, reader
 * and pretty printer for anything JSON.</p>
 *
 * <p>Compared to the basic Jackson's {@link
 * com.fasterxml.jackson.databind.ObjectMapper}, the one provided by {@link
 * com.github.fge.jackson.JacksonUtils} deserializes all floating point numbers
 * as {@link java.math.BigDecimal}s by default. This is done using {@link
 * com.fasterxml.jackson.databind.DeserializationFeature#USE_BIG_DECIMAL_FOR_FLOATS}.</p>
 */
package com.github.fge.jackson;
