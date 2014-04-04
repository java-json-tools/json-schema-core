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

package com.github.fge.jsonschema.core.util.equivalence;

import com.google.common.base.Equivalence;

/**
 * Small wrapper class over Guava's builtin {@link Equivalence}s
 *
 * <p>Guava's {@link Equivalence#equals()} and {@link Equivalence#identity()}
 * are not parameterized: this just makes them so.</p>
 */
public final class Equivalences
{
    private Equivalences()
    {
    }

    /**
     * Return a parameterized {@link Equivalence#equals()}
     *
     * @param <T> the parameter type
     * @return the parameterized equivalence
     */
    @SuppressWarnings("unchecked")
    public static <T> Equivalence<T> equals()
    {
        return (Equivalence<T>) Equivalence.equals();
    }

    /**
     * Return a parameterized {@link Equivalence#identity()}
     *
     * @param <T> the parameter type
     * @return the parameterized equivalence
     */
    @SuppressWarnings("unchecked")
    public static <T> Equivalence<T> identity()
    {
        return (Equivalence<T>) Equivalence.identity();
    }
}
