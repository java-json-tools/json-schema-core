package com.github.fge.jsonschema.keyword;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class SchemaSelectorModuleTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void cannotInsertNullDescriptorInModule()
    {
        try {
            new SchemaSelectorModule() {
                {
                    addDescriptor(null, false);
                }
            };
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("schemaSelector.nullDescriptor"));
        }
    }

    @Test
    public void cannotOverrideExistingDescriptor()
    {
        final URI location = SchemaVersion.DRAFTV4.getLocation();
        final SchemaDescriptor descriptor = SchemaDescriptor.newBuilder()
            .setLocator(location).freeze();

        try {
            new SchemaSelectorModule() {
                {
                    addDescriptor(descriptor, false);
                }
            };
            fail("No exception thrown!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("schemaSelector.duplicateSchema", location));
        }
    }

    @Test
    public void cannotSetNullDefaultVersion()
    {
        try {
            new SchemaSelectorModule() {
                {
                    setDefaultVersion(null);
                }
            };
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("schemaSelector.nullVersion"));
        }
    }
}
