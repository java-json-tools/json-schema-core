package com.github.fge.jsonschema.library;

import com.github.fge.jsonschema.exceptions.unchecked.DictionaryBuildError;
import com.github.fge.jsonschema.report.ProcessingMessage;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static com.github.fge.jsonschema.messages.DictionaryBuildErrors.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class DictionaryBuilderTest
{
    private static final String KEY = "key";
    private static final Whatever MOCK1 = mock(Whatever.class);
    private static final Whatever MOCK2 = mock(Whatever.class);

    private DictionaryBuilder<Whatever> builder;

    @BeforeMethod
    public void createBuilder()
    {
        builder = Dictionary.newBuilder();
    }

    @Test
    public void cannotInsertNullKey()
    {
        try {
            builder.addEntry(null, null);
            fail("No exception thrown!!");
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_KEY);
        }
    }

    @Test
    public void cannotInsertNullValue()
    {
        try {
            builder.addEntry(KEY, null);
            fail("No exception thrown!!");
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_VALUE);
        }
    }

    @Test
    public void cannotImportFromNullDictionary()
    {
        try {
            builder.addAll(null);
            fail("No exception thrown!!");
        } catch (DictionaryBuildError e) {
            final ProcessingMessage message = e.getProcessingMessage();
            assertMessage(message).hasMessage(NULL_DICT);
        }
    }

    @Test
    public void insertedValueCanBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        assertSame(builder.freeze().get(KEY), MOCK1);
    }

    @Test
    public void removedValueCannotBeRetrieved()
    {
        builder.addEntry(KEY, MOCK1);
        builder.removeEntry(KEY);
        assertNull(builder.freeze().get(KEY));
    }

    @Test
    public void valuesCanBeOverwritten()
    {
        builder.addEntry(KEY, MOCK1);
        builder.addEntry(KEY, MOCK2);
        assertSame(builder.freeze().get(KEY), MOCK2);
    }

    private interface Whatever
    {
    }
}
