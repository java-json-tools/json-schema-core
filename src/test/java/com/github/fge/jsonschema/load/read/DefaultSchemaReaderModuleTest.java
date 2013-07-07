package com.github.fge.jsonschema.load.read;

import com.fasterxml.jackson.core.JsonParser;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.io.Closer;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.testng.Assert.*;

public final class DefaultSchemaReaderModuleTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    @Test
    public void defaultReaderModuleRefusesNullDereferencing()
    {
        try {
            new DefaultSchemaReaderModule() {
                {
                    setDereferencing(null);
                }
            };
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("loadingCfg.nullDereferencingMode"));
        }
    }

    @Test
    public void defaultReaderModuleRefusesNullParserOption()
    {
        try {
            new DefaultSchemaReaderModule() {
                {
                    addParserFeature(null);
                }
            };
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("loadingCfg.nullJsonParserFeature"));
        }
    }



    @Test(dependsOnMethods = "defaultReaderModuleRefusesNullParserOption")
    public void defaultReaderModuleAcknowledgesParserOptions()
        throws IOException
    {
        final SchemaReaderModule module = new DefaultSchemaReaderModule() {
            {
                addParserFeature(JsonParser.Feature.ALLOW_COMMENTS);
                addParserFeature(JsonParser.Feature.ALLOW_SINGLE_QUOTES);
                addParserFeature(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES);
            }
        };

        final SchemaReader reader = module.getReader();

        final URL url = DefaultSchemaReaderModuleTest.class
            .getResource("/load/nonstandard-source.json");

        if (url == null)
            throw new IOException("resource not found");

        final Closer closer = Closer.create();
        final InputStream in;

        try {
            in = closer.register(url.openStream());
            reader.read(in);
        } finally {
            closer.close();
        }
        assertTrue(true);
    }

}
