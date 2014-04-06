/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of both licenses is available at the root of this project (under the
 * names LGPL-3.0.txt and ASL-2.0.txt respectively) or, if you have a jar instead,
 * in the META-INF/ directory.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.load;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.collect.Lists;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class URIManagerTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private URIDownloader mock;

    @BeforeMethod
    public void setUp()
    {
        mock = mock(URIDownloader.class);
    }

    @Test
    public void unhandledSchemeShouldBeReportedAsSuch()
    {
        final URI uri = URI.create("bar://baz");
        final URIManager manager = new URIManager();

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("refProcessing.unhandledScheme",
                    "bar", uri))
                .hasField("scheme", "bar").hasField("uri", uri)
                .hasLevel(LogLevel.FATAL);
        }
    }

    @Test
    public void downloaderProblemsShouldBeReportedAsSuch()
        throws IOException
    {
        final URI uri = URI.create("foo://bar");
        final Exception foo = new IOException("foo");

        when(mock.fetch(any(URI.class))).thenThrow(foo);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", mock).freeze();

        final URIManager manager = new URIManager(cfg);

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("uriManager.uriIOError", uri))
                .hasField("uri", uri).hasLevel(LogLevel.FATAL)
                .hasField("exceptionMessage", "foo");
        }
    }

    @Test
    public void nonJSONInputShouldBeReportedAsSuch()
        throws IOException
    {
        final URI uri = URI.create("foo://bar");
        final InputStream sampleStream
            = new ByteArrayInputStream("}".getBytes());

        when(mock.fetch(any(URI.class))).thenReturn(sampleStream);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", mock).freeze();

        final URIManager manager = new URIManager(cfg);

        try {
            manager.getContent(uri);
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("uriManager.uriNotJson", uri))
                .hasTextField("parsingMessage").hasLevel(LogLevel.FATAL)
                .hasField("uri", uri);
        }
    }

    @Test
    void managerParsesNonstandardJSON()
        throws IOException, ProcessingException
    {
        // get resource URIs for standard and nonstandard sources
        final String wellFormed = "resource:/load/standard-source.json";
        final URI uri1 = JsonRef.fromString(wellFormed).getLocator();
        final String illFormed = "resource:/load/nonstandard-source.json";
        final URI uri2 = JsonRef.fromString(illFormed).getLocator();

        // get URIManager configured to parse nonstandard sources
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addParserFeature(JsonParser.Feature.ALLOW_COMMENTS)
            .addParserFeature(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
            .addParserFeature(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES)
            .freeze();
        final URIManager manager = new URIManager(cfg);

        // load JSON nodes from sources using nonstandard manager
        final JsonNode node1 = manager.getContent(uri1);
        final JsonNode node2 = manager.getContent(uri2);

        // validate correctness of loaded equivalent sources
        assertTrue(JsonNumEquals.getInstance().equivalent(node1, node2));
    }

    @DataProvider
    public Iterator<Object[]> bizarreInputs()
    {
        final List<Object[]> list = Lists.newArrayList();

        list.add(new Object[] { "[][]", "uriManager.extraneousValue" });

        return list.iterator();
    }

    @Test(dataProvider = "bizarreInputs")
    public void managerDealsCorrectlyWithBizarreInput(final String input,
        final String message)
        throws IOException
    {
        final URIDownloader downloader = mock(URIDownloader.class);
        final ByteArrayInputStream stream
            = new ByteArrayInputStream(input.getBytes());
        when(downloader.fetch(any(URI.class))).thenReturn(stream);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader).freeze();

        final URIManager manager = new URIManager(cfg);
        try {
            manager.getContent(URI.create("foo://bar"));
            fail("No exception thrown!!");
        } catch (ProcessingException e) {
            final String msg = BUNDLE.printf(message, "foo://bar");
            assertEquals(e.getProcessingMessage().getMessage(), msg);
        }
    }

}
