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

package com.github.fge.jsonschema.report;

import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;

public final class ProcessingReportTest
{
    /*
     * All levels except fatal
     */
    private static final EnumSet<LogLevel> LEVELS
        = EnumSet.complementOf(EnumSet.of(LogLevel.FATAL, LogLevel.NONE));

    @DataProvider
    public Iterator<Object[]> getLogLevels()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final LogLevel level: LEVELS)
            list.add(new Object[] { level });

        // We don't want the values in the same order repeatedly, so...
        Collections.shuffle(list);

        return list.iterator();
    }
}
