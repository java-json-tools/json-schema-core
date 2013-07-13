package com.github.fge.jsonschema.loader;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.fge.jsonschema.exceptions.ProcessingException;
import com.github.fge.jsonschema.loader.downloaders.URIDownloader;
import com.github.fge.jsonschema.loader.downloaders.URIDownloadersRegistry;
import com.github.fge.jsonschema.loader.read.SchemaReader;
import com.github.fge.jsonschema.messages.JsonSchemaCoreMessageBundle;
import com.github.fge.jsonschema.report.ProcessingMessage;
import com.github.fge.jsonschema.tree.SchemaTree;
import com.github.fge.jsonschema.util.InjectedWith;
import com.github.fge.msgsimple.bundle.MessageBundle;
import com.github.fge.msgsimple.load.MessageBundles;
import com.google.common.annotations.Beta;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Map;

import static com.github.fge.jsonschema.util.InjectedWith.Injection;

@Beta
@InjectedWith({
    @Injection(SchemaReader.class),
    @Injection(URIDownloadersRegistry.class)
})
final class DefaultSchemaLoader
    implements SchemaLoader
{
    private static final MessageBundle BUNDLE
        = MessageBundles.getBundle(JsonSchemaCoreMessageBundle.class);

    private final SchemaReader reader;
    private final Map<String, URIDownloader> downloaders;

    @Inject
    DefaultSchemaLoader(final SchemaReader reader,
        final URIDownloadersRegistry registry)
    {
        this.reader = reader;
        downloaders = registry.build();
    }

    @Override
    public SchemaTree load(final URI uri)
        throws ProcessingException
    {
        final String scheme = uri.getScheme();
        final URIDownloader downloader = downloaders.get(scheme);

        if (downloader == null)
            throw new ProcessingException(new ProcessingMessage()
            .setMessage(BUNDLE.getMessage("refProcessing.unhandledScheme"))
            .putArgument("scheme", scheme));

        final InputStream in;
        try {
            in = downloader.fetch(uri);
        } catch (IOException e) {
            throw new ProcessingException(new ProcessingMessage()
            .setMessage(BUNDLE.getMessage("refProcessing.uriIOError"))
            .putArgument("uri", uri)
            .put("exceptionClass", e.getClass().getCanonicalName())
            .put("exceptionMessage", e.getMessage()));
        }

        try {
            return reader.read(in);
        } catch (JsonProcessingException e) {
            final String msg = e.getOriginalMessage();
            throw new ProcessingException(new ProcessingMessage()
            .setMessage(BUNDLE.getMessage("refProcessing.uriNotJson"))
            .putArgument("uri", uri).put("parsingMessage", msg));
        } catch (IOException e) {
            throw new ProcessingException(new ProcessingMessage()
                .setMessage(BUNDLE.getMessage("refProcessing.uriIOError"))
                .putArgument("uri", uri)
                .put("exceptionClass", e.getClass().getCanonicalName())
                .put("exceptionMessage", e.getMessage()));
        }
    }
}
