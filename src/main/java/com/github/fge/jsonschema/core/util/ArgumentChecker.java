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

package com.github.fge.jsonschema.core.util;

import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;

import javax.annotation.Nullable;

/**
 * An argument checker
 *
 * <p>The only method of this interface returns nothing; it is supposed to throw
 * a {@link RuntimeException} or derivate if the argument supplied is not legal.
 * Typically, this will be {@link IllegalArgumentException} or, if you disallow
 * null arguments, {@link NullPointerException}.</p>
 *
 * <p>Note that the argument to check <b>may</b> be {@code null}.</p>
 *
 * @param <T> type of the argument to check
 *
 * @since 1.1.9
 */
@Beta
public abstract class ArgumentChecker<T>
{
    protected static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    /**
     * As its name says
     *
     * @param <X> the type of the argument
     * @return an argument checker
     */
    public static <X> ArgumentChecker<X> anythingGoes()
    {
        return new ArgumentChecker<X>()
        {
            @Override
            public void check(final X argument)
            {
            }
        };
    }

    /**
     * An argument checker refusing null arguments; fails with a standard
     * message
     *
     * @param <X> the type of the argument
     * @return an argument checker
     */
    public static <X> ArgumentChecker<X> notNull()
    {
        return new ArgumentChecker<X>()
        {
            @Override
            public void check(@Nullable final X argument)
            {
                BUNDLE.checkNotNull(argument, "argChecker.notNull");
            }
        };
    }

    /**
     * An argument checker refusing null arguments; fails with a customized
     * message
     *
     * @param message the message to fail with
     * @param <X> the type of the argument
     * @return an argument checker
     * @throws NullPointerException message is null
     */
    public static <X> ArgumentChecker<X> notNull(final String message)
    {
        BUNDLE.checkNotNull(message, "argChecker.nullMessage");
        return new ArgumentChecker<X>()
        {
            @Override
            public void check(@Nullable final X argument)
            {
                if (argument == null)
                    throw new NullPointerException(message);
            }
        };
    }

    /**
     * Check the sanity of an argument
     *
     * @param argument the argument
     * @throws RuntimeException see description
     */
    public abstract void check(@Nullable final T argument);
}
