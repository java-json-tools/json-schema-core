package com.github.fge.jsonschema.load.transform;

import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class URITransformerBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private URITransformerBuilder builder;

    @BeforeMethod
    public void initBuilder()
    {
        builder = URITransformer.newBuilder();
    }

    @Test
    public void nullNamespaceIsNotAccepted()
    {
        try {
            builder.setNamespace((URI) null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriChecks.nullInput"));
        }

        try {
            builder.setNamespace((String) null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("uriChecks.nullInput"));
        }
    }
}
