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

package com.github.fge.jsonschema.load;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.registry.resolve.URIDownloader;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

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
                .hasMessage(BUNDLE.printf("refProcessing.uriIOError", uri))
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
                .hasMessage(BUNDLE.printf("refProcessing.uriNotJson", uri))
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

    @Test
    public void managerIgnoresAttemptToRemoveAutoCloseSource()
        throws IOException, ProcessingException
    {
        // FIXME: worked even with the culprit feature disabled??
        final String content = "{\"hello\":false} 32";
        final URIDownloader downloader = mock(URIDownloader.class);
        final ByteArrayInputStream stream
            = spy(new ByteArrayInputStream(content.getBytes()));
        when(downloader.fetch(any(URI.class))).thenReturn(stream);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader)
            .removeParserFeature(JsonParser.Feature.AUTO_CLOSE_SOURCE)
            .freeze();

        final URIManager manager = new URIManager(cfg);
        manager.getContent(URI.create("foo://bar"));
        verify(stream).close();
    }
}
