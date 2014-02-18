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

package com.github.fge.jsonschema.core.load.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class LoadingConfigurationBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final URIDownloader downloader = mock(URIDownloader.class);
    private final LoadingConfigurationBuilder cfg
        = LoadingConfiguration.newBuilder();

    @Test
    public void cannotRegisterIllegalScheme()
    {
        final String scheme = "+24";
        try {
            cfg.addScheme(scheme, downloader);
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("loadingCfg.illegalScheme", scheme));
        }
    }

    @Test
    public void registeringAndUnregisteringSchemeWorks()
    {
        final String scheme = "foo";

        cfg.addScheme(scheme, downloader);
        assertNotNull(cfg.freeze().getDownloaderMap().get(scheme));

        cfg.removeScheme(scheme);
        assertNull(cfg.freeze().getDownloaderMap().get(scheme));
    }

    @Test
    public void cannotSetNullDereferencingMode()
    {
        try {
            cfg.dereferencing(null);
            fail("No exception thrown!!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("loadingCfg.nullDereferencingMode"));
        }
    }

    @DataProvider
    public Iterator<Object[]> schemaVersions()
    {
        final List<Object[]> list = Lists.newArrayList();

        for (final SchemaVersion version: SchemaVersion.values())
            list.add(new Object[] { version });

        return list.iterator();
    }

    // Mysteriously fails _only some times_ when run with gradle...
    @Test(dataProvider = "schemaVersions", enabled = false)
    public void basicConfigurationContainsCoreSchemas(
        final SchemaVersion version)
    {
        final Map<URI, JsonNode> map = cfg.freeze().getPreloadedSchemas();

        final JsonNode actual = map.get(version.getLocation());
        final JsonNode expected = version.getSchema();
        assertTrue(JsonNumEquals.getInstance().equivalent(actual, expected));
    }

    @Test
    public void cannotOverwriteAnAlreadyPresentSchema()
    {
        final String input = "http://json-schema.org/draft-04/schema#";
        try {
            cfg.preloadSchema(input, JacksonUtils.nodeFactory().objectNode());
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
                BUNDLE.printf("loadingCfg.duplicateURI", input));
        }
    }

    @Test
    public void cannotPreloadSchemaWithoutTopLevelId()
    {
        try {
            cfg.preloadSchema(JacksonUtils.nodeFactory().objectNode());
            fail("No exception thrown!!");
        } catch (IllegalArgumentException e) {
            assertEquals(e.getMessage(),
               BUNDLE.getMessage("loadingCfg.noIDInSchema"));
        }
    }
}
