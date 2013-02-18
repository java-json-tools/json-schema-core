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

package com.github.fge.jsonschema.messages;

import com.github.fge.jsonschema.processing.ProcessingCache;
import com.github.fge.jsonschema.processing.ProcessorChain;
import com.github.fge.jsonschema.processing.ProcessorMap;
import com.github.fge.jsonschema.processing.ProcessorSelector;

/**
 * Messages used by processing helper classes
 *
 * @see com.github.fge.jsonschema.processing
 */
public enum ProcessingErrors
{
    /**
     * No suitable processor found by a selector
     *
     * @see ProcessorSelector
     * @see ProcessorMap
     */
    NO_SUITABLE_PROCESSOR("no suitable processor found"),
    /**
     * Attempt to use a null predicate
     *
     * @see ProcessorSelector
     */
    NULL_PREDICATE("predicate cannot be null"),
    /**
     * Attempt to use a null processor
     *
     * @see ProcessorSelector
     * @see ProcessorMap
     * @see ProcessorChain
     */
    NULL_PROCESSOR("processor cannot be null"),
    /**
     * Attempt to use a null key in a map selector
     *
     * @see ProcessorMap
     */
    NULL_KEY("map keys must not be null"),
    /**
     * Attempt to use a null function in a map selector
     *
     * @see ProcessorMap
     */
    NULL_FUNCTION("input-to-key function must not be null"),
    /**
     * Attempt to use a null equivalence in a cache
     *
     * @see ProcessingCache
     */
    NULL_EQUIVALENCE("equivalence must not be null"),
    /**
     * Attempt to use a null cache loader in a processing cache
     *
     * @see ProcessingCache
     */
    NULL_LOADER("cache loader must not be null"),
    /**
     * Attempt to load a null key from a processing cache
     *
     * @see ProcessingCache
     */
    NULL_KEYS_FORBIDDEN("null keys are not allowed"),
    /**
     * Chain deliberately stopped
     *
     * @see ProcessorChain#failOnError()
     */
    CHAIN_STOPPED("processing chain stopped"),
    ;

    private final String message;

    ProcessingErrors(final String message)
    {
        this.message = message;
    }

    @Override
    public String toString()
    {
        return message;
    }
}
