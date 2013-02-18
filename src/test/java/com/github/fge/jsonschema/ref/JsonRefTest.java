package com.github.fge.jsonschema.ref;

import com.github.fge.jsonschema.exceptions.JsonReferenceException;
import com.github.fge.jsonschema.exceptions.unchecked.JsonReferenceError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.Test;

import java.net.URISyntaxException;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.JsonReferenceMessages.*;
import static org.testng.Assert.*;

public final class JsonRefTest
{
    @Test
    public void cannotCreateRefFromNullURI()
    {
        try {
            JsonRef.fromURI(null);
            fail("No exception thrown!!");
        } catch (JsonReferenceError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_URI);
        }
    }

    @Test
    public void cannotCreateRefFromNullString()
        throws JsonReferenceException
    {
        try {
            JsonRef.fromString(null);
            fail("No exception thrown!!");
        } catch (JsonReferenceError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_INPUT);
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
            assertMessage(message).hasMessage(INVALID_URI)
                .hasField("input", input)
                .hasField("exceptionClass", URISyntaxException.class.getName());
        }
    }
}
