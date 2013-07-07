package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public final class KeywordDescriptorTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void cannotCreateKeywordWithNullName()
    {
        try {
            KeywordDescriptor.withName(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullName"));
        }
    }

    @Test(dependsOnMethods = "cannotCreateKeywordWithNullName")
    public void cannotSubmitNullPointerCollector()
    {
        try {
            KeywordDescriptor.withName("foo").setPointerCollector(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullPointerCollector"));
        }
    }

    @Test(dependsOnMethods = "cannotCreateKeywordWithNullName")
    public void cannotSubmitNullSyntaxChecker()
    {
        try {
            KeywordDescriptor.withName("foo").setSyntaxChecker(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("keywordDescriptor.nullSyntaxChecker"));
        }
    }
}
