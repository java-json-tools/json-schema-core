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

package com.github.fge.jsonschema.core.load.configuration;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
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

    @Test(dataProvider = "schemaVersions")
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
