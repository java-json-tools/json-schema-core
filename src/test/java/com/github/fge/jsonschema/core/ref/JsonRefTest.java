/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available under the src/resources/ directory of
 * this project (under the names LGPL-3.0.txt and ASL-2.0.txt respectively).
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.ref;

import com.github.fge.jsonschema.core.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.report.ProcessingMessage;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.testng.Assert.*;

public final class JsonRefTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void cannotCreateRefFromNullURI()
    {
        try {
            JsonRef.fromURI(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(), BUNDLE.getMessage("jsonRef.nullURI"));
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
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("jsonRef.nullInput"));
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
                .hasMessage(BUNDLE.printf("jsonRef.invalidURI", input))
                .hasField("input", input)
                .hasField("exceptionClass", URISyntaxException.class.getName());
        }
    }
}
