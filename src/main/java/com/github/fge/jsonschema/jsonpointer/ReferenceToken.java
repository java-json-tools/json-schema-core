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

import com.github.fge.jsonschema.report.ProcessingMessage;
import com.google.common.collect.ImmutableList;

import java.util.List;

import static com.github.fge.jsonschema.messages.JsonPointerMessages.*;

public final class ReferenceToken
{
    private static final char ESCAPE = '~';

    private static final List<Character> ENCODED = ImmutableList.of('0', '1');
    private static final List<Character> DECODED = ImmutableList.of('~', '/');

    private final String cooked;
    private final String raw;

    private ReferenceToken(final String cooked, final String raw)
    {
        this.cooked = cooked;
        this.raw = raw;
    }

    public static ReferenceToken fromCooked(final String cooked)
        throws JsonPointerException
    {
        return new ReferenceToken(cooked, asRaw(cooked));
    }

    public static ReferenceToken fromRaw(final String raw)
    {
        return new ReferenceToken(asCooked(raw), raw);
    }

    public static ReferenceToken fromInt(final int index)
    {
        final String s = Integer.toString(index);
        return new ReferenceToken(s, s);
    }

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

    private static String asRaw(final String cooked)
        throws JsonPointerException
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
            throw new JsonPointerException(new ProcessingMessage()
                .message(EMPTY_ESCAPE));

        return raw.toString();
    }

    private static void appendEscaped(final StringBuilder sb, final char c)
        throws JsonPointerException
    {
        final int index = ENCODED.indexOf(c);
        if (index == -1)
            throw new JsonPointerException(new ProcessingMessage()
                .message(ILLEGAL_ESCAPE).put("valid", ENCODED)
                .put("found", Character.valueOf(c)));

        sb.append(DECODED.get(index));
    }

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
