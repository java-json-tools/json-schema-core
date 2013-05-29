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

package com.github.fge.jsonschema.ref;

import com.github.fge.jsonschema.CoreMessageBundle;
import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class JsonRefTest
{
    private static final CoreMessageBundle BUNDLE
        = CoreMessageBundle.getInstance();

    @Test
    public void cannotCreateRefFromNullURI()
    {
        try {
            JsonRef.fromURI(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("jsonRef.nullURI"));
        }
    }

    @Test
    public void cannotCreateRefFromNullString()
        throws JsonReferenceException
    {
        try {
            JsonRef.fromString(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getKey("jsonRef.nullInput"));
        }
    }

    @Test
    public void illegalURIThrowsAnException()
    {
        final String input = "+24:";

        try {
            JsonRef.fromString(input);
            fail("No exception thrown!!");
        } catch (JsonReferenceException e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message)
                .hasMessage(BUNDLE.getKey("jsonRef.invalidURI"))
                .hasField("input", input)
                .hasField("exceptionClass", URISyntaxException.class.getName());
        }
    }
}
