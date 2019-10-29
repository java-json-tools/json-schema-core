/*
 * Copyright (c) 2014, Francis Galiegue (fgaliegue@gmail.com)
 *
 * This software is dual-licensed under:
 *
 * - the Lesser General Public License (LGPL) version 3.0 or, at your option, any
 *   later version;
 * - the Apache Software License (ASL) version 2.0.
 *
 * The text of this file and of both licenses is available at the root of this
 * project or, if you have the jar distribution, in directory META-INF/, under
 * the names LGPL-3.0.txt and ASL-2.0.txt respectively.
 *
 * Direct link to the sources:
 *
 * - LGPL 3.0: https://www.gnu.org/licenses/lgpl-3.0.txt
 * - ASL 2.0: http://www.apache.org/licenses/LICENSE-2.0.txt
 */

package com.github.fge.jsonschema.core.load;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.github.fge.jackson.JacksonUtils;
import com.github.fge.jsonschema.core.exceptions.ProcessingException;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfiguration;
import com.github.fge.jsonschema.core.load.configuration.LoadingConfigurationBuilder;
import com.github.fge.jsonschema.core.load.uri.URITranslatorConfiguration;
import com.github.fge.jsonschema.core.load.download.URIDownloader;
import com.github.fge.jsonschema.core.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.core.ref.JsonRef;
import com.github.fge.jsonschema.core.report.LogLevel;
import com.github.fge.jsonschema.core.tree.SchemaTree;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import static com.github.fge.jsonschema.matchers.ProcessingMessageAssert.*;
import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

public final class SchemaLoaderTest
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private static final byte[] BYTES = JacksonUtils.nodeFactory().objectNode()
        .toString().getBytes(UTF_8);

    @Test
    public void namespacesAreRespected()
        throws ProcessingException, IOException
    {
        final URI fullPath = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader()
        {
            @Override
            public InputStream fetch(final URI source)
                throws IOException
            {
                if (!fullPath.equals(source))
                    throw new IOException();
                return new ByteArrayInputStream(BYTES);
            }
        });

        final String namespace = "foo:///bar/../bar/";
        final URITranslatorConfiguration translatorCfg
            = URITranslatorConfiguration.newBuilder()
                .setNamespace(namespace).freeze();
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader)
            .setURITranslatorConfiguration(translatorCfg)
            .freeze();

        final URI rootns = URI.create(namespace);

        final SchemaLoader loader = new SchemaLoader(cfg);

        final URI uri = URI.create("../baz");
        loader.get(uri);
        final JsonRef ref = JsonRef.fromURI(rootns.resolve(uri));
        verify(downloader).fetch(rootns.resolve(ref.toURI()));
    }

    @Test
    public void URIsAreNormalizedBehindTheScenes()
        throws ProcessingException
    {
        final String location = "http://toto/a/../b";
        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .preloadSchema(location, JacksonUtils.nodeFactory().objectNode())
            .freeze();

        final SchemaLoader loader = new SchemaLoader(cfg);

        final SchemaTree tree = loader.get(URI.create(location));

        assertEquals(tree.getLoadingRef().toURI(),
                URI.create("http://toto/b#"));
    }

    @Test
    public void NonAbsoluteURIsAreRefused()
    {
        final SchemaLoader loader = new SchemaLoader();

        final URI target = URI.create("moo#");

        try {
            loader.get(target);
            fail("No exception thrown!");
        } catch (ProcessingException e) {
            assertMessage(e.getProcessingMessage())
                .hasMessage(BUNDLE.printf("refProcessing.uriNotAbsolute",
                    target))
                .hasLevel(LogLevel.FATAL).hasField("uri", target);
        }
    }

    @Test
    public void preloadedSchemasAreNotFetchedAgain()
        throws ProcessingException, IOException
    {
        final String location = "http://foo.bar/baz#";
        final URI uri = URI.create(location);
        final URIDownloader mock = mock(URIDownloader.class);
        final LoadingConfigurationBuilder builder = LoadingConfiguration
            .newBuilder().addScheme("http", mock)
            .preloadSchema(location, JacksonUtils.nodeFactory().objectNode());

        LoadingConfiguration cfg;
        SchemaLoader registry;

        cfg = builder.freeze();
        registry = new SchemaLoader(cfg);
        registry.get(uri);
        verify(mock, never()).fetch(uri);

        //even if cache is disabled
        cfg = builder.setCacheSize(0).freeze();
        registry = new SchemaLoader(cfg);
        registry.get(uri);
        verify(mock, never()).fetch(uri);        
    }

    @Test
    public void schemasAreFetchedOnceNotTwice()
        throws ProcessingException, IOException
    {
        final URI uri = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader() {
            @Override
            public InputStream fetch(final URI source)
                    throws IOException {
                return new ByteArrayInputStream(BYTES);
            }
        });

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader).freeze();
        final SchemaLoader loader = new SchemaLoader(cfg);

        loader.get(uri);
        loader.get(uri);
        verify(downloader, times(1)).fetch(uri);
    }
    
    @Test
    public void schemasCacheCanBeDisabled()
        throws ProcessingException, IOException
    {
        final URI uri = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader() {
            @Override
            public InputStream fetch(final URI source)
                    throws IOException {
                return new ByteArrayInputStream(BYTES);
            }
        });

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader)
            .setCacheSize(0)
            .freeze();
        final SchemaLoader loader = new SchemaLoader(cfg);

        loader.get(uri);
        loader.get(uri);
        verify(downloader, times(2)).fetch(uri);
    }

    @Test
    public void schemasCacheCanBeDisabledViaCacheSize()
        throws ProcessingException, IOException
    {
        final URI uri = URI.create("foo:/baz#");
        final URIDownloader downloader = spy(new URIDownloader()
        {
            @Override
            public InputStream fetch(final URI source)
                throws IOException
            {
                return new ByteArrayInputStream(BYTES);
            }
        });

        final LoadingConfiguration cfg = LoadingConfiguration.newBuilder()
            .addScheme("foo", downloader)
            .setCacheSize(0)
            .freeze();
        final SchemaLoader loader = new SchemaLoader(cfg);

        loader.get(uri);
        loader.get(uri);
        verify(downloader, times(2)).fetch(uri);
    }
}
