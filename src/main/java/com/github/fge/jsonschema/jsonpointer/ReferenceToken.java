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

package com.github.fge.jsonschema.jsonpointer;

import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.jcip.annotations.Immutable;

import java.util.List;

import static com.github.fge.jsonschema.jsonpointer.JsonPointerMessages.*;
import static com.github.fge.jsonschema.messages.JsonReferenceMessages.*;

/**
 * One JSON Pointer reference token
 *
 * <p>This class represents one reference token. It has no publicly available
 * constructor; instead, it has static factory methods used to generate tokens
 * depending on whether the input is a decoded (raw) token or an encoded
 * (cooked) one, or even an integer.</p>
 *
 * <p>The only characters to encode in a raw token are {@code /} (which becomes
 * {@code ~1}) and {@code ~} (which becomes {@code ~0}).</p>
 *
 * <p>Note that a reference token <b>may</b> be empty (empty object member names
 * are legal!).</p>
 */
@Immutable
public final class ReferenceToken
{
    /**
     * The escape character in a cooked token
     */
    private static final char ESCAPE = '~';

    /**
     * List of encoding characters in a cooked token
     */
    private static final List<Character> ENCODED = ImmutableList.of('0', '1');

    /**
     * List of sequences to encode in a raw token
     *
     * <p>This list and {@link #ENCODED} have matching indices on purpose.</p>
     */
    private static final List<Character> DECODED = ImmutableList.of('~', '/');

    /**
     * The cooked representation of that token
     *
     * @see #toString()
     */
    private final String cooked;

    /**
     * The raw representation of that token
     *
     * @see #hashCode()
     * @see #equals(Object)
     */
    private final String raw;

    /**
     * The only constructor, private by design
     *
     * @param cooked the cooked representation of that token
     * @param raw the raw representation of that token
     */
    private ReferenceToken(final String cooked, final String raw)
    {
        this.cooked = cooked;
        this.raw = raw;
    }

    /**
     * Generate a reference token from an encoded (cooked) representation
     *
     * @param cooked the input
     * @return a token
     * @throws JsonReferenceException illegal token (bad encode sequence)
     * @throws NullPointerException null input
     */
    public static ReferenceToken fromCooked(final String cooked)
        throws JsonReferenceException
    {
        Preconditions.checkNotNull(cooked, NULL_INPUT);
        return new ReferenceToken(cooked, asRaw(cooked));
    }

    /**
     * Generate a reference token from a decoded (raw) representation
     *
     * @param raw the input
     * @return a token
     * @throws NullPointerException null input
     */
    public static ReferenceToken fromRaw(final String raw)
    {
        Preconditions.checkNotNull(raw, NULL_INPUT);
        return new ReferenceToken(asCooked(raw), raw);
    }

    /**
     * Generate a reference token from an integer
     *
     * @param index the integer
     * @return a token
     */
    public static ReferenceToken fromInt(final int index)
    {
        final String s = Integer.toString(index);
        return new ReferenceToken(s, s);
    }

    /**
     * Get the raw representation of that token as a string
     *
     * @return the raw representation (for traversing purposes)
     */
    public String getRaw()
    {
        return raw;
    }

    @Override
    public int hashCode()
    {
        return raw.hashCode();
    }

    @Override
    public boolean equals(final Object obj)
    {
        if (obj == null)
            return false;
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        final ReferenceToken other = (ReferenceToken) obj;
        return raw.equals(other.raw);
    }

    @Override
    public String toString()
    {
        return cooked;
    }

    /**
     * Decode an encoded token
     *
     * @param cooked the encoded token
     * @return the decoded token
     * @throws JsonReferenceException bad encoded representation
     */
    private static String asRaw(final String cooked)
        throws JsonReferenceException
    {
        final StringBuilder raw = new StringBuilder(cooked.length());

        String s = cooked;
        boolean inEscape = false;
        char c;

        while (!s.isEmpty()) {
            c = s.charAt(0);
            s = s.substring(1);
            if (inEscape) {
                appendEscaped(raw, c);
                inEscape = false;
                continue;
            }
            if (c == ESCAPE) {
                inEscape = true;
                continue;
            }
            raw.append(c);
        }

        if (inEscape)
            throw new JsonReferenceException(EMPTY_ESCAPE.asMessage());

        return raw.toString();
    }

    /**
     * Append a decoded sequence to a {@link StringBuilder}
     *
     * @param sb the string builder to append to
     * @param c the escaped character
     * @throws JsonReferenceException illegal escaped character
     */
    private static void appendEscaped(final StringBuilder sb, final char c)
        throws JsonReferenceException
    {
        final int index = ENCODED.indexOf(c);
        if (index == -1)
            throw new JsonReferenceException(ILLEGAL_ESCAPE.asMessage()
                .put("valid", ENCODED).put("found", Character.valueOf(c)));

        sb.append(DECODED.get(index));
    }

    /**
     * Encode a raw token
     *
     * @param raw the raw representation
     * @return the cooked, encoded representation
     */
    private static String asCooked(final String raw)
    {
        final StringBuilder cooked = new StringBuilder(raw.length());

        String s = raw;
        char c;
        int index;

        while (!s.isEmpty()) {
            c = s.charAt(0);
            s = s.substring(1);
            index = DECODED.indexOf(c);
            if (index != -1)
                cooked.append('~').append(ENCODED.get(index));
            else
                cooked.append(c);
        }

        return cooked.toString();
    }
}
