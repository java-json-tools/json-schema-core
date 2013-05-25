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

package com.github.fge.jsonschema.util;

import javax.annotation.concurrent.NotThreadSafe;

/**
 * Interface for a thawed object
 *
 * <p>This interface is closely coupled with {@link Frozen}. The principle is
 * simple: you can obtain a thawed object from a frozen one, and a thawed object
 * can generate a frozen one.</p>
 *
 * <p>Implementations of this interface are mutable and offer no guarantee of
 * thread safety.</p>
 *
 * @param <F> the type of the frozen representation
 */
@NotThreadSafe
public interface Thawed<F extends Frozen<? extends Thawed<F>>>
{
    /**
     * Obtain a frozen representation of this thawed object
     *
     * @return a frozen, immutable object
     */
    F freeze();
}
