package com.github.fge.jsonschema.util.equivalence;

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
