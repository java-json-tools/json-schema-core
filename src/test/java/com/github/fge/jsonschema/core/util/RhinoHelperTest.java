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

package com.github.fge.jsonschema.core.util;

import com.google.common.collect.ImmutableList;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Iterator;

import static org.testng.Assert.*;

public final class RhinoHelperTest
{
    @DataProvider
    public Iterator<Object[]> ecma262regexes()
    {
        return ImmutableList.of(
            new Object[] { "[^]", true },
            new Object[] { "(?<=foo)bar", false },
            new Object[] { "", true },
            new Object[] { "[a-z]+(?!foo)(?=bar)", true }
        ).iterator();
    }

    @Test(
        dataProvider = "ecma262regexes",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public void regexesAreCorrectlyAnalyzed(final String regex,
        final boolean valid)
    {
        assertEquals(RhinoHelper.regexIsValid(regex), valid);
    }

    @DataProvider
    public Iterator<Object[]> regexTestCases()
    {
        return ImmutableList.of(
            new Object[] { "[^a-z]", "9am", true },
            new Object[] { "bar\\d+", "foobar19ae", true },
            new Object[] { "^bar\\d+", "bar", false },
            new Object[] { "[a-z]+(?!foo)(?=bar)", "3aaaabar", true }
        ).iterator();
    }

    @Test(
        dataProvider = "regexTestCases",
        invocationCount = 10,
        threadPoolSize = 4
    )
    public void regexMatchingIsDoneCorrectly(final String regex,
        final String input, final boolean valid)
    {
        assertEquals(RhinoHelper.regMatch(regex, input), valid);
    }
}
