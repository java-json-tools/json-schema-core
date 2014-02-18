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

package com.github.fge.jsonschema.core.keyword;

import com.github.fge.jsonschema.SchemaVersion;
import com.github.fge.jsonschema.core.schema.SchemaDescriptor;
import com.github.fge.jsonschema.core.schema.SchemaSelectorConfiguration;
import com.github.fge.jsonschema.core.schema.SchemaSelectorConfigurationBuilder;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.net.URI;

import static org.testng.Assert.*;

public final class SchemaSelectorConfigurationBuilderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private SchemaSelectorConfigurationBuilder builder;

    @BeforeMethod
    public void init()
    {
        builder = SchemaSelectorConfiguration.newBuilder();
    }

    @Test
    public void cannotInsertNullDescriptorInModule()
    {
        try {
            builder.addDescriptor(null, false);
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
            builder.addDescriptor(descriptor, false);
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
            builder.setDefaultVersion(null);
            fail("No exception thrown!");
        } catch (NullPointerException e) {
            assertEquals(e.getMessage(),
                BUNDLE.getMessage("schemaSelector.nullVersion"));
        }
    }
}
