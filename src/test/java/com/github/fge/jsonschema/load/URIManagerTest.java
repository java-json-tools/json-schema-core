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
import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jackson.JsonNumEquals;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.ref.JsonRef;
import com.github.fge.jsonschema.report.LogLevel;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.serviceloader.MessageBundleFactory;
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
        = MessageBundleFactory.getBundle(JsonSchemaCoreMessageBundle.class);

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
    public void URIRedirectionIsFollowed()
        throws IOException, ProcessingException
    {
        /*
         * The content we return
         */
        final JsonNode expected = JacksonUtils.nodeFactory().objectNode()
            .put("hello", "world");
        final InputStream sampleStream
            = new ByteArrayInputStream(expected.toString().getBytes());

        /*
         * We need to build both the source URI and destination URI. As they are
         * both transformed to valid JSON References internally, we also build
         * JsonRef-compatible URIs (ie, with a fragment, even empty).
         *
         * The user, however, may supply URIs which are not JsonRef-compatible.
         */
        final String source = "http://some.site/schema.json";
        final String destination = "foo://real/location.json";
        final URI sourceURI = JsonRef.fromString(source).getLocator();
        final URI destinationURI = JsonRef.fromString(destination).getLocator();

        /*
         * Build another mock for the original source URI protocol, make it
         * return the same thing as the target URI. Register both downloaders.
         */
        when(mock.fetch(destinationURI)).thenReturn(sampleStream);
        final URIDownloader httpMock = mock(URIDownloader.class);
        when(httpMock.fetch(sourceURI)).thenReturn(sampleStream);

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("http", httpMock).addScheme("foo", mock)
            .addSchemaRedirect(source, destination).freeze();

        final URIManager manager = new URIManager(cfg);

        /*
         * Get the original source...
         */
        final JsonNode actual = manager.getContent(sourceURI);

        /*
         * And verify that it has been downloaded from the target, not the
         * source
         */
        verify(httpMock, never()).fetch(any(URI.class));
        verify(mock).fetch(destinationURI);

        /*
         * Finally, ensure the correctness of the downloaded content.
         */
        assertEquals(actual, expected);
    }

    @Test
    void managerParsesNonstandardJSON()
        throws IOException, ProcessingException
    {
        // get resource URIs for standard and nonstandard sources
        final String standardSource = "resource:/load/standard-source.json";
        final URI standardSourceURI = JsonRef.fromString(standardSource).getLocator();
        final String nonstandardSource = "resource:/load/nonstandard-source.json";
        final URI nonstandardSourceURI = JsonRef.fromString(nonstandardSource).getLocator();

        // get URIManager configured to parse nonstandard sources
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
                .addJsonParserFeature(JsonParser.Feature.ALLOW_COMMENTS)
                .addJsonParserFeature(JsonParser.Feature.ALLOW_SINGLE_QUOTES)
                .addJsonParserFeature(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES).freeze();
        final URIManager manager = new URIManager(cfg);

        // load JSON nodes from sources using nonstandard manager
        final JsonNode standardJSON = manager.getContent(standardSourceURI);
        final JsonNode nonstandardJSON = manager.getContent(nonstandardSourceURI);

        // validate correctness of loaded equivalent sources
        assertTrue(JsonNumEquals.getInstance().equivalent(standardJSON, nonstandardJSON));
    }
}
